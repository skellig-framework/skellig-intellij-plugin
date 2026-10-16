package org.skellig.plugin.language.teststep;

import com.intellij.formatting.Block;
import com.intellij.formatting.FormatTextRanges;
import com.intellij.formatting.Formatter;
import com.intellij.formatting.FormatterImpl;
import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.formatting.FormattingModelEx;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.mock.MockApplication;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepParserDefinition;
import org.skellig.plugin.language.teststep.psi.formatter.SkelligTestStepBlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkelligTestStepFormatterTest {
    private final ParserFixture fixture = new ParserFixture();

    @BeforeEach void setUp(TestInfo info) throws Exception {
        fixture.setName(info.getTestMethod().orElseThrow().getName());
        fixture.setUp();
        var application = (MockApplication) ApplicationManager.getApplication();
        application.registerService(Formatter.class, new FormatterImpl());
        application.getExtensionArea().registerExtensionPoint("com.intellij.documentWriteAccessGuard",
            com.intellij.openapi.editor.impl.DocumentWriteAccessGuard.class.getName(),
            com.intellij.openapi.extensions.ExtensionPoint.Kind.INTERFACE);
    }

    @AfterEach void tearDown() throws Exception { fixture.tearDown(); }

    @Test void nestedMultilineObjectArguments() {
        assertFormatted("""
            name ("example") {
            message=convert(
            true,
            {
            c=decode(
            {
            msg="text"
            },
            64
            )
            },
            false
            )
            }
            """, """
            name ("example") {
                message = convert(
                    true,
                    {
                        c = decode(
                            {
                                msg = "text"
                            },
                            64
                        )
                    },
                    false
                )
            }
            """);
    }

    @Test void inlineObjectsAndReferences() {
        assertFormatted("""
            name("inline"){
            msg=toJson({code=get(step).values.code},64,${configProperty})
            values{a[1,2,3]}
            }
            """, """
            name ("inline") {
                msg = toJson({ code = get(step).values.code }, 64, ${configProperty})
                values { a [1, 2, 3] }
            }
            """);
    }

    @Test void commentsEmptyObjectsAndBlankLines() {
        assertFormatted("""
            name ("comments") {
            // before message
            message=convert(
            {}, // empty argument
            {
            // inside object
            a=1

            b=2
            }
            )
            }
            """, """
            name ("comments") {
                // before message
                message = convert(
                    {}, // empty argument
                    {
                        // inside object
                        a = 1

                        b = 2
                    }
                )
            }
            """);
    }

    @Test void objectArgumentOpeningOnCallLine() {
        assertFormatted("""
            name ("example") {
            message=toJson({
            eventCode=get(addEventTest).values
            success=true
            nested {
            name="event"
            }
            })
            }
            """, """
            name ("example") {
                message = toJson({
                    eventCode = get(addEventTest).values
                    success = true
                    nested {
                        name = "event"
                    }
                })
            }
            """);
    }

    @Test void multilineArraysAndPropertyMaps() {
        assertFormatted("""
            name ("arrays") {
            values
            {
            list [
            {
            name="first"
            },
            [1,2],
            {}
            ]
            }
            }
            """, """
            name ("arrays") {
                values
                {
                    list [
                        {
                            name = "first"
                        },
                        [1, 2],
                        {}
                    ]
                }
            }
            """);
    }

    @Test void enterIndentationForEmptyAndIncompleteContainers() {
        var file = fixture.createPsiFile("typing", "name (\"example\") { message = convert({");
        var map = PsiTreeUtil.findChildOfType(file, org.skellig.plugin.language.teststep.psi.SkelligTestStepMap.class);
        assertNotNull(map, "retain object PSI while its closing brace is missing");
        assertEquals(Indent.getNormalIndent().getType(),
            new SkelligTestStepBlock(map.getNode(), Indent.getNoneIndent()).getChildAttributes(1).getChildIndent().getType());
        var function = PsiTreeUtil.findChildOfType(file, org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression.class);
        assertNotNull(function);
        assertEquals(Indent.getContinuationIndent().getType(),
            new SkelligTestStepBlock(function.getNode(), Indent.getNoneIndent()).getChildAttributes(2).getChildIndent().getType());
    }

    @Test void tabsAreWhitespaceAndIndentOptionsAreRespected() {
        assertFormatted("name (\"tabs\") {\n\tvalues {\n\t\ta = 1\n\t}\n}\n", """
            name ("tabs") {
                values {
                    a = 1
                }
            }
            """);
        var options = new CommonCodeStyleSettings.IndentOptions();
        options.INDENT_SIZE = 2;
        options.CONTINUATION_INDENT_SIZE = 4;
        String input = "name (\"two spaces\") {\nmsg = convert(\ntrue\n)\n}\n";
        String expected = "name (\"two spaces\") {\n  msg = convert(\n      true\n  )\n}\n";
        assertEquals(expected, format(input, options));
        assertEquals(expected, format(expected, options));
    }

    @Test void emptyMultilineFunctionAndArray() {
        assertFormatted("""
            name ("empty") {
            msg=empty(
            )
            values [
            ]
            }
            """, """
            name ("empty") {
                msg = empty(
                )
                values [
                ]
            }
            """);
    }

    private void assertFormatted(String input, String expected) {
        String actual = format(input);
        assertEquals(expected, actual);
        assertEquals(actual, format(actual), "formatting must be idempotent");
    }

    private String format(String text) {
        var indents = new CommonCodeStyleSettings.IndentOptions();
        indents.INDENT_SIZE = 4;
        indents.CONTINUATION_INDENT_SIZE = 4;
        return format(text, indents);
    }

    private String format(String text, CommonCodeStyleSettings.IndentOptions indents) {
        PsiFile file = fixture.createPsiFile("formatting", text);
        assertTrue(PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class).isEmpty(), file.getText());
        var model = new TextFormattingModel(new SkelligTestStepBlock(file.getNode(), Indent.getNoneIndent()), text);
        var settings = new TestSettings();
        ((FormatterImpl) Formatter.getInstance()).format(model, settings, indents,
            new FormatTextRanges(new TextRange(0, text.length()), true));
        return model.document.getText();
    }

    // Exercise IntelliJ's real formatting engine with parsed STS blocks. Only the
    // document adapter is minimal, avoiding unrelated IDE startup and project services.
    private static final class TextFormattingModel implements FormattingModelEx {
        private final Block root;
        private final Document document;
        private final FormattingDocumentModel documentModel;

        TextFormattingModel(Block root, String text) {
            this.root = root;
            this.document = new DocumentImpl(text, true);
            this.documentModel = new FormattingDocumentModel() {
                public int getLineNumber(int offset) { return document.getLineNumber(offset); }
                public int getLineStartOffset(int line) { return document.getLineStartOffset(line); }
                public CharSequence getText(TextRange range) { return document.getCharsSequence().subSequence(range.getStartOffset(), range.getEndOffset()); }
                public int getTextLength() { return document.getTextLength(); }
                public Document getDocument() { return document; }
                public boolean containsWhiteSpaceSymbolsOnly(int start, int end) {
                    return document.getCharsSequence().subSequence(start, end).chars().allMatch(Character::isWhitespace);
                }
                public CharSequence adjustWhiteSpaceIfNecessary(CharSequence whitespace, int start, int end, ASTNode node, boolean changed) { return whitespace; }
            };
        }

        public Block getRootBlock() { return root; }
        public FormattingDocumentModel getDocumentModel() { return documentModel; }
        public TextRange replaceWhiteSpace(TextRange range, String whitespace) {
            document.replaceString(range.getStartOffset(), range.getEndOffset(), whitespace);
            return new TextRange(range.getStartOffset(), range.getStartOffset() + whitespace.length());
        }
        public TextRange replaceWhiteSpace(TextRange range, ASTNode node, String whitespace) { return replaceWhiteSpace(range, whitespace); }
        public TextRange shiftIndentInsideRange(ASTNode node, TextRange range, int shift) { return range; }
        public void commitChanges() { }
    }

    private static final class TestSettings extends CodeStyleSettings {
        TestSettings() { super(false, false); }
    }

    private static final class ParserFixture extends ParsingTestCase {
        ParserFixture() { super("", "sts", new SkelligTestStepParserDefinition()); }
        @Override protected String getTestDataPath() { return "src/test/testData"; }
        @Override public void setUp() throws Exception { super.setUp(); }
        @Override public void tearDown() throws Exception { super.tearDown(); }
        @Override public PsiFile createPsiFile(String name, String text) { return super.createPsiFile(name, text); }
    }
}
