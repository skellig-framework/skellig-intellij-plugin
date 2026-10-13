package org.skellig.plugin.language.teststep;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepMap;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepParserDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkelligTestStepParserTest {
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
    public void testObjectArgument() {
        assertParsed("""
            message = toJson({
                eventCode = get(addEventTest).values.eventCode
                success = true
            })
            """, 2, 1);
    }

    @Test
    public void testNestedObjectAndMixedArguments() {
        PsiFile file = assertParsed("msg = toXML(true, { c = decode({ msg = \"fdsasdf\" }, 64) })", 2, 2);
        for (var function : PsiTreeUtil.findChildrenOfType(file, SkelligTestStepFunctionExpression.class)) {
            assertEquals(2, function.getArgList().size());
        }
    }

    @Test
    public void testMultilineAndEmptyObjects() {
        assertParsed("""
            msg = convert(
                {},
                { c = decode({}, 64) },
                false
            )
            """, 2, 3);
    }

    @Test
    public void testExistingExpressionArguments() {
        assertParsed("msg = convert(true, 64, \"text\", get(step).values.code, empty())", 3, 0);
    }

    private PsiFile assertParsed(String property, int functions, int maps) {
        PsiFile file = fixture.createPsiFile("example", "name(\"example\") {\n" + property + "\n}");
        assertEquals(java.util.List.of(),
            PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class).stream()
                .map(error -> error.getErrorDescription() + " at " + error.getTextOffset()).toList(), file.getText());
        assertEquals(functions, PsiTreeUtil.findChildrenOfType(file, SkelligTestStepFunctionExpression.class).size());
        assertEquals(maps, PsiTreeUtil.findChildrenOfType(file, SkelligTestStepMap.class).size());
        return file;
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
