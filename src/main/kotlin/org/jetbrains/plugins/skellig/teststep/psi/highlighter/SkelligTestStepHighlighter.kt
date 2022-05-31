package org.jetbrains.plugins.skellig.teststep.psi.highlighter

import org.jetbrains.annotations.NonNls
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors

object SkelligTestStepHighlighter {

    @NonNls
    val COMMENT_ID = "TEST_STEP_COMMENT"
    val COMMENT = TextAttributesKey.createTextAttributesKey(
        COMMENT_ID,
        DefaultLanguageHighlighterColors.DOC_COMMENT
    )

    @NonNls
    val KEYWORD_ID = "TEST_STEP_KEYWORD"
    val KEYWORD = TextAttributesKey.createTextAttributesKey(
        KEYWORD_ID,
        DefaultLanguageHighlighterColors.KEYWORD
    )

    @NonNls
    val BRACKETS_ID = "TEST_STEP_BRACKETS"
    val BRACKETS = TextAttributesKey.createTextAttributesKey(
        BRACKETS_ID,
        DefaultLanguageHighlighterColors.BRACKETS
    )

    @NonNls
    val TEST_STEP_REGEXP_PARAMETER_ID = "TEST_STEP_REGEXP_PARAMETER"
    val PARAMETER = TextAttributesKey.createTextAttributesKey(
        TEST_STEP_REGEXP_PARAMETER_ID,
        DefaultLanguageHighlighterColors.PARAMETER
    )

    @NonNls
    val TEST_STEP_STRING_ID = "TEST_STEP_STRING"
    val STRING = TextAttributesKey.createTextAttributesKey(
        TEST_STEP_STRING_ID,
        DefaultLanguageHighlighterColors.STRING
    )
    val TEXT = TextAttributesKey.createTextAttributesKey("TEST_STEP_TEXT", HighlighterColors.TEXT)

    val PROPERTY = TextAttributesKey.createTextAttributesKey("TEST_STEP_PROPERTY", DefaultLanguageHighlighterColors.PARAMETER)
}