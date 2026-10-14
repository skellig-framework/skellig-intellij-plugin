package org.skellig.plugin.language.teststep;

import com.intellij.openapi.Disposable;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.ExtensionTestUtil;
import com.intellij.testFramework.IndexingTestUtil;
import com.intellij.testFramework.TestApplicationManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepValue;
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepFindUsagesProvider;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SkelligTestStepReferenceTest {
    @AfterAll static void releaseLightProject() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> com.intellij.openapi.application.WriteIntentReadAction.run(
            com.intellij.testFramework.LightPlatformTestCase::closeAndDeleteProject));
    }

    @Test void resolvesAcrossFilesAndFindsAllGetUsages() throws Exception {
        withFixture("crossFileReferences", fixture -> {
            PsiFile declaration = fixture.addFile("declaration.sts", """
                name("test step") {
                    id = addEventTest
                    values { id = nestedId }
                }
                """);
            PsiFile calls = fixture.addFile("calls.sts", """
                name("caller") {
                    a = get(addEventTest).values
                    b = toJson({ c = get("addEventTest", false) })
                    c = get(
                        addEventTest,
                        3,
                        50
                    )
                    ignored = other(addEventTest)
                    text = "get(addEventTest)"
                    // get(addEventTest)
                }
                """);
            fixture.ready();
            ReadAction.run(() -> {
                var id = PsiTreeUtil.findChildrenOfType(declaration, SkelligTestStepValue.class).stream()
                    .filter(value -> "addEventTest".equals(value.getName())).findFirst().orElseThrow();
                assertNotNull(id.getNameIdentifier());
                assertTrue(new SkelligTestStepFindUsagesProvider().canFindUsagesFor(id));
                var functions = PsiTreeUtil.findChildrenOfType(calls, SkelligTestStepFunctionExpression.class);
                for (var function : functions) {
                    PsiReference reference = function.getReference();
                    if (function.getFunctionName().getText().equals("get")) {
                        assertNotNull(reference);
                        assertSame(id, reference.resolve());
                        assertTrue(reference.isReferenceTo(id));
                        String key = reference.getRangeInElement().substring(function.getText());
                        assertEquals("addEventTest", key);
                        assertNotNull(calls.findReferenceAt(function.getTextOffset() + reference.getRangeInElement().getStartOffset()));
                    } else {
                        assertNull(reference);
                    }
                }
                var usages = ReferencesSearch.search(id, GlobalSearchScope.projectScope(fixture.project())).findAll();
                assertEquals(3, usages.size(), usages.toString());
                assertTrue(usages.stream().allMatch(ref -> ref.isReferenceTo(id)));
            });
        });
    }

    @Test void distinguishesTopLevelIdsAndDuplicateDeclarations() throws Exception {
        withFixture("duplicateIds", fixture -> {
            fixture.addFile("first.sts", "name(\"first\") { id = shared }");
            fixture.addFile("second.sts", "name(\"second\") { id = shared }");
            PsiFile file = fixture.addFile("caller.sts", """
                name("caller") {
                    values { id = nestedOnly }
                    a = get(shared)
                    b = get(nestedOnly)
                    c = get(missing)
                    d = get(${dynamic})
                    e = get()
                }
                """);
            fixture.ready();
            ReadAction.run(() -> {
                var functions = PsiTreeUtil.findChildrenOfType(file, SkelligTestStepFunctionExpression.class);
                for (var function : functions) {
                    var reference = function.getReference();
                    switch (function.getText()) {
                        case "get(shared)" -> {
                            assertNotNull(reference);
                            assertNull(reference.resolve());
                            var results = ((PsiPolyVariantReference) reference).multiResolve(false);
                            assertEquals(2, results.length);
                            for (var result : results) assertTrue(reference.isReferenceTo(result.getElement()));
                        }
                        case "get(nestedOnly)", "get(missing)" -> {
                            assertNotNull(reference);
                            assertNull(reference.resolve());
                        }
                        default -> assertNull(reference);
                    }
                }
            });
        });
    }

    @Test void keepsStateFallbackAndOffersIdsInCompletion() throws Exception {
        withFixture("stateAndCompletion", fixture -> {
            PsiFile declaration = fixture.addFile("declaration.sts", """
                name("source") {
                    id = stepId
                    state {
                        storedKey = true
                        stepId = false
                    }
                }
                """);
            PsiFile calls = fixture.addFile("calls.sts", """
                name("caller") {
                    a = get(stepId)
                    b = get(storedKey)
                }
                """);
            fixture.ready();
            ReadAction.run(() -> {
                var functions = PsiTreeUtil.findChildrenOfType(calls, SkelligTestStepFunctionExpression.class).stream().toList();
                var stepReference = functions.get(0).getReference();
                assertNotNull(stepReference);
                assertInstanceOf(SkelligTestStepValue.class, stepReference.resolve());
                var stateReference = functions.get(1).getReference();
                assertNotNull(stateReference);
                var stateKey = assertInstanceOf(org.skellig.plugin.language.teststep.psi.SkelligTestStepKey.class, stateReference.resolve());
                assertEquals("storedKey", stateKey.getName());
                assertTrue(new SkelligTestStepFindUsagesProvider().canFindUsagesFor(stateKey));
                assertEquals(1, ReferencesSearch.search(stateKey).findAll().size());
                var shadowedKey = PsiTreeUtil.findChildrenOfType(declaration,
                        org.skellig.plugin.language.teststep.psi.SkelligTestStepKey.class).stream()
                    .filter(key -> "stepId".equals(key.getName())).findFirst().orElseThrow();
                assertTrue(ReferencesSearch.search(shadowedKey).findAll().isEmpty());
                var names = java.util.Arrays.stream(stepReference.getVariants())
                    .map(variant -> ((LookupElement) variant).getLookupString()).collect(java.util.stream.Collectors.toSet());
                assertEquals(java.util.Set.of("stepId", "storedKey"), names);
                assertSame(declaration, stepReference.resolve().getContainingFile());
            });
        });
    }

    @Test void quotedIdFindsUsagesAndUpdatesOnRename() throws Exception {
        withFixture("quotedIdRename", fixture -> {
            PsiFile declaration = fixture.addFile("declaration.sts", "name(\"source\") { id = \"quotedStep\" }");
            PsiFile calls = fixture.addFile("calls.sts", """
                name("caller") {
                    a = get(quotedStep)
                    b = get("quotedStep")
                }
                """);
            fixture.ready();
            SkelligTestStepValue id = ReadAction.compute(() ->
                PsiTreeUtil.findChildrenOfType(declaration, SkelligTestStepValue.class).stream()
                    .filter(value -> "quotedStep".equals(value.getName())).findFirst().orElseThrow());
            fixture.rename(id, "renamedStep");
            fixture.ready();
            ReadAction.run(() -> {
                assertTrue(declaration.getText().contains("id = \"renamedStep\""));
                assertTrue(calls.getText().contains("get(renamedStep)"));
                assertTrue(calls.getText().contains("get(\"renamedStep\")"));
                var renamed = PsiTreeUtil.findChildrenOfType(declaration, SkelligTestStepValue.class).stream()
                    .filter(value -> "renamedStep".equals(value.getName())).findFirst().orElseThrow();
                assertEquals(2, ReferencesSearch.search(renamed).findAll().size());
                for (var call : PsiTreeUtil.findChildrenOfType(calls, SkelligTestStepFunctionExpression.class)) {
                    assertSame(renamed, call.getReference().resolve());
                }
            });
        });
    }

    @Test void stateKeyFindsUsagesAndRenamesOnlyMatchingGetCalls() throws Exception {
        withFixture("stateRename", fixture -> {
            PsiFile source = fixture.addFile("state.sts", """
                name("source") { state { storedKey = true } }
                """);
            PsiFile calls = fixture.addFile("calls.sts", """
                name("caller") {
                    a = get(storedKey)
                    b = get("storedKey")
                    c = other(storedKey)
                }
                """);
            fixture.ready();
            var key = ReadAction.compute(() -> PsiTreeUtil.findChildrenOfType(source,
                    org.skellig.plugin.language.teststep.psi.SkelligTestStepKey.class).stream()
                .filter(candidate -> "storedKey".equals(candidate.getName())).findFirst().orElseThrow());
            fixture.rename(key, "updatedKey");
            fixture.ready();
            ReadAction.run(() -> {
                assertTrue(source.getText().contains("updatedKey = true"));
                assertTrue(calls.getText().contains("get(updatedKey)"));
                assertTrue(calls.getText().contains("get(\"updatedKey\")"));
                assertTrue(calls.getText().contains("other(storedKey)"));
            });
        });
    }

    @Test void sameFileReferencesTrackIdEdits() throws Exception {
        withFixture("sameFileEdits", fixture -> {
            PsiFile file = fixture.addFile("same.sts", """
                name("source") { id = localStep }
                name("caller") { a = get(localStep) }
                """);
            fixture.ready();
            ReadAction.run(() -> {
                var call = PsiTreeUtil.findChildOfType(file, SkelligTestStepFunctionExpression.class);
                assertNotNull(call);
                assertNotNull(call.getReference().resolve());
            });
            fixture.replace(file, file.getText().replace("id = localStep", "id = changedStep"));
            fixture.ready();
            ReadAction.run(() -> {
                assertTrue(file.getText().contains("id = changedStep"), file.getText());
                var call = PsiTreeUtil.findChildOfType(file, SkelligTestStepFunctionExpression.class);
                assertNotNull(call);
                assertNull(call.getReference().resolve());
            });
        });
    }

    private void withFixture(String name, Consumer<Fixture> test) throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            TestApplicationManager.getInstance();
            Disposable activities = Disposer.newDisposable("reference test startup activities");
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
        PsiFile addFile(String name, String text) { return myFixture.addFileToProject(name, text); }
        com.intellij.openapi.project.Project project() { return getProject(); }
        void rename(PsiElement target, String name) { myFixture.renameElement(target, name); }
        void replace(PsiFile file, String text) {
            com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(getProject(), () -> {
                var manager = com.intellij.psi.PsiDocumentManager.getInstance(getProject());
                var document = manager.getDocument(file);
                assertNotNull(document);
                document.setText(text);
                manager.commitDocument(document);
            });
        }
        void ready() { IndexingTestUtil.waitUntilIndexesAreReady(getProject()); }
    }
}
