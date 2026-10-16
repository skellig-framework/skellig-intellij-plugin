package org.skellig.plugin.language.teststep;

import com.intellij.openapi.Disposable;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.ExtensionTestUtil;
import com.intellij.testFramework.TestApplicationManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SkelligTestStepCompletionTest {
    @AfterAll static void releaseLightProject() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> com.intellij.openapi.application.WriteIntentReadAction.run(
            com.intellij.testFramework.LightPlatformTestCase::closeAndDeleteProject));
    }

    @Test void offersAllReceiverFunctionsAfterCallsAndProperties() throws Exception {
        withFixture("chainCompletion", fixture -> {
            for (String receiver : List.of("get(step)", "get(step).values", "get(step).values.msg", "${response}", "$", "$result", "get(step).\"skellig-db\"")) {
                fixture.configure("name(\"caller\") { validate { result = " + receiver + ".<caret> } }");
                var names = fixture.complete();
                assertEquals(java.util.Set.of(
                    "add", "all", "any", "between", "contains", "count", "find", "findAll", "findLast", "first",
                    "fromIndex", "fromRegex", "getValues", "groupBy", "jsonPath", "jsonToList", "jsonToMap", "map",
                    "match", "maxOf", "minOf", "none", "size", "sort", "subString", "subStringLast", "sumOf",
                    "toBigDecimal", "toBoolean", "toByte", "toBytes", "toDate", "toDateTime", "toDouble", "toFloat",
                    "toInstant", "toInt", "toJson", "toLong", "toShort", "toString", "xpath"), names, receiver);
            }
        });
    }

    @Test void filtersPrefixesAndInsertsFunctionCalls() throws Exception {
        withFixture("chainInsertion", fixture -> {
            fixture.configure("name(\"caller\") { result = get(step).values.to<caret> }");
            assertTrue(new org.skellig.plugin.language.teststep.psi.completion.SkelligTestStepCompletionContributor()
                .invokeAutoPopup(fixture.elementAtCaret(), '.'));
            var names = fixture.complete();
            assertTrue(names.contains("toInt"));
            assertTrue(names.contains("toString"));
            assertFalse(names.contains("xpath"));
            fixture.choose("toInt");
            assertEquals("name(\"caller\") { result = get(step).values.toInt() }", fixture.text());
            assertEquals(fixture.text().indexOf("toInt(") + "toInt(".length(), fixture.caret());

            fixture.configure("name(\"caller\") { result = get(step).to<caret>() }");
            fixture.complete();
            fixture.choose("toString");
            assertEquals("name(\"caller\") { result = get(step).toString() }", fixture.text());
        });
    }

    @Test void doesNotOfferChainedFunctionsInStringsCommentsOrDeclarations() throws Exception {
        withFixture("completionContexts", fixture -> {
            for (String text : List.of(
                "name(\"get(step).<caret>\") { }",
                "name(\"caller\") { result = \"get(step).<caret>\" }",
                "name(\"caller\") { // get(step).<caret>\n }",
                "name(\"caller\") { result = 1.<caret> }",
                "name(\"caller\") { result = .<caret> }",
                "name(\"caller\") { <caret> }")) {
                fixture.configure(text);
                assertFalse(fixture.complete().contains("xpath"), text);
            }
            fixture.configure("na<caret>");
            fixture.complete();
            assertTrue(fixture.text().contains("name (\"Test name\") {\n}"), fixture.text());
            fixture.configure("name(\"caller\") { values { na<caret> } }");
            fixture.complete();
            assertFalse(fixture.text().contains("Test name"), fixture.text());
        });
    }

    private void withFixture(String name, Consumer<Fixture> test) throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            TestApplicationManager.getInstance();
            Disposable activities = Disposer.newDisposable("completion test startup activities");
            // Language navigation does not need project startup integrations from
            // the installed IDE (including unrelated Ultimate integrations).
            ExtensionTestUtil.maskExtensions(ExtensionPointName.create("com.intellij.postStartupActivity"),
                List.of(), activities, false);
            Fixture fixture = new Fixture();
            fixture.setName(name);
            try {
                fixture.setUp();
                try {
                    test.accept(fixture);
                } finally {
                    fixture.tearDown();
                }
            } finally {
                Disposer.dispose(activities);
            }
        });
    }

    private static final class Fixture extends BasePlatformTestCase {
        @Override public void setUp() throws Exception { super.setUp(); }
        @Override public void tearDown() throws Exception { super.tearDown(); }
        void configure(String text) { myFixture.configureByText("completion.sts", text); }
        java.util.Set<String> complete() {
            var items = myFixture.completeBasic();
            if (items == null) return java.util.Set.of();
            return java.util.Arrays.stream(items).map(LookupElement::getLookupString)
                .collect(java.util.stream.Collectors.toSet());
        }
        void choose(String name) {
            var lookup = myFixture.getLookup();
            lookup.setCurrentItem(lookup.getItems().stream().filter(item -> item.getLookupString().equals(name)).findFirst().orElseThrow());
            myFixture.finishLookup('\n');
        }
        com.intellij.psi.PsiElement elementAtCaret() { return myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1); }
        String text() { return myFixture.getEditor().getDocument().getText(); }
        int caret() { return myFixture.getCaretOffset(); }
    }
}
