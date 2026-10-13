package org.skellig.plugin.language.teststep;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepParserDefinition;

import org.skellig.plugin.language.teststep.psi.highlighter.SkelligTestStepFunctionAnnotator;
import org.skellig.plugin.language.teststep.psi.highlighter.SkelligTestStepHighlighter;
import org.skellig.plugin.language.teststep.psi.highlighter.SkelligTestStepSyntaxHighlighter;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkelligTestStepHighlighterTest {
    private final ParserFixture fixture = new ParserFixture();

    @BeforeEach
    void setUp(TestInfo testInfo) throws Exception {
        fixture.setName(testInfo.getTestMethod().orElseThrow().getName());
        fixture.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        fixture.tearDown();
    }

    @Test
    public void testKeywordsOnlyAtDeclarationAndDirectPropertyKeys() {
        assertKeywordHighlighting("""
            <keyword>name</keyword> ("Event service is listening for the booking events") {
                <keyword>message</keyword> = toJson({
                    eventCode = get(addEventTest).values
                    success = true
                })
                <keyword>values</keyword> {
                    msg {
                        name = "event 1"
                        values = get(step).message
                    }
                }
                <keyword>test</keyword> = get(step).values
                <keyword>messages</keyword> [ { name = "event 2" } ]
                result = values
                values.name = get(step).name
            }
            """);
    }

    @Test
    public void testAllExistingKeywordPropertiesAndNestedNames() {
        assertKeywordHighlighting("""
            <keyword>name</keyword> ("keywords") {
                <keyword>state</keyword> = state
                <keyword>validate</keyword> { body.name = "event" }
                <keyword>where</keyword> { values = 1 }
                <keyword>payload</keyword> { request = "event" }
                <keyword>body</keyword> { message = "event" }
                <keyword>request</keyword> { validate = "event" }
                <keyword>name</keyword> = "property"
            }
            <keyword>name</keyword> ("next") {
                <keyword>values</keyword> { name = "event" }
            }
            """);
    }

    @Test
    public void testResultKeywordsInsideValidationBlocks() {
        assertKeywordHighlighting("""
            <keyword>name</keyword> ("validation") {
                <keyword>validate</keyword> {
                    <keyword>$</keyword>."skellig-db".fromIndex(0).taken_seats.contains(${1}) = true
                    <keyword>$result</keyword>.success = true
                    actual = compare(<keyword>$</keyword>, <keyword>$result</keyword>, ${result})
                    nested { actual = <keyword>$result</keyword> }
                    other = $resultOther
                    text = "$result and ${1}"
                    // $result and $
                }
                assert {
                    <keyword>$result</keyword>.success = true
                    <keyword>$</keyword>.success = true
                    expected = ${result}
                }
            }
            """);
    }

    @Test
    public void testResultKeywordsDoNotLeakOutsideValidationBlocks() {
        assertKeywordHighlighting("""
            <keyword>name</keyword> ("outside validation") {
                actual = $result
                <keyword>values</keyword> {
                    actual = $
                    validate { actual = $result }
                    assert { actual = $result }
                }
                <keyword>validate</keyword> { actual = <keyword>$result</keyword> }
                actual = $
                assert { actual = <keyword>$</keyword> }
                actual = $result
            }
            """);
    }

    private void assertKeywordHighlighting(String markedText) {
        Set<TextRange> expected = new LinkedHashSet<>();
        StringBuilder text = new StringBuilder();
        int cursor = 0;
        while (markedText.indexOf("<keyword>", cursor) >= 0) {
            int start = markedText.indexOf("<keyword>", cursor);
            int end = markedText.indexOf("</keyword>", start);
            text.append(markedText, cursor, start);
            int offset = text.length();
            text.append(markedText, start + "<keyword>".length(), end);
            expected.add(new TextRange(offset, text.length()));
            cursor = end + "</keyword>".length();
        }
        text.append(markedText.substring(cursor));
        PsiFile file = fixture.createPsiFile("highlighting", text.toString());
        assertEquals(java.util.List.of(),
            PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class).stream()
                .map(PsiErrorElement::getErrorDescription).toList());

        var highlighter = new SkelligTestStepSyntaxHighlighter();
        var lexer = highlighter.getHighlightingLexer();
        Set<TextRange> actual = new LinkedHashSet<>();
        lexer.start(text);
        while (lexer.getTokenType() != null) {
            if (java.util.Arrays.asList(highlighter.getTokenHighlights(lexer.getTokenType()))
                .contains(SkelligTestStepHighlighter.INSTANCE.getKEYWORD())) {
                actual.add(new TextRange(lexer.getTokenStart(), lexer.getTokenEnd()));
            }
            lexer.advance();
        }
        var holder = new AnnotationHolderImpl(new AnnotationSession(file), false);
        var annotator = new SkelligTestStepFunctionAnnotator();
        file.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                holder.runAnnotatorWithContext(element, annotator);
                super.visitElement(element);
            }
        });
        for (var annotation : holder) {
            var range = new TextRange(annotation.getStartOffset(), annotation.getEndOffset());
            TextAttributesKey key = annotation.getTextAttributes();
            if (key == SkelligTestStepHighlighter.INSTANCE.getKEYWORD()) {
                actual.add(range);
            } else {
                actual.removeIf(range::contains);
            }
        }
        assertEquals(expected, actual, text.toString());
    }

    // Reuse IntelliJ's parser setup while Jupiter owns test discovery and lifecycle.
    private static final class ParserFixture extends ParsingTestCase {
        private ParserFixture() {
            super("", "sts", new SkelligTestStepParserDefinition());
        }

        @Override
        protected String getTestDataPath() {
            return "src/test/testData";
        }

        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        @Override
        public void tearDown() throws Exception {
            super.tearDown();
        }

        @Override
        public PsiFile createPsiFile(String name, String text) {
            return super.createPsiFile(name, text);
        }
    }
}
