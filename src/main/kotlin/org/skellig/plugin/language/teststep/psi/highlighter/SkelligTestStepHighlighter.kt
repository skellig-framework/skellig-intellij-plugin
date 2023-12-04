package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors

object SkelligTestStepHighlighter {

    val COMMENT = TextAttributesKey.createTextAttributesKey(
        "TEST_STEP_COMMENT",
        DefaultLanguageHighlighterColors.DOC_COMMENT
    )

    val KEYWORD = TextAttributesKey.createTextAttributesKey(
        "TEST_STEP_KEYWORD",
        DefaultLanguageHighlighterColors.KEYWORD
    )

    val BRACKETS = TextAttributesKey.createTextAttributesKey(
        "TEST_STEP_BRACKETS",
        DefaultLanguageHighlighterColors.BRACKETS
    )

    val REFERENCE = TextAttributesKey.createTextAttributesKey(
        "REFERENCE",
        DefaultLanguageHighlighterColors.PARAMETER
    )

    val STRING = TextAttributesKey.createTextAttributesKey(
        "TEST_STEP_STRING",
        DefaultLanguageHighlighterColors.STRING
    )

    val TEXT = TextAttributesKey.createTextAttributesKey("TEST_STEP_TEXT", HighlighterColors.TEXT)

    val FUNCTION = TextAttributesKey.createTextAttributesKey(
        "FUNCTION",
        DefaultLanguageHighlighterColors.FUNCTION_CALL
    )

    val OPERATION = TextAttributesKey.createTextAttributesKey(
        "OPERATION",
        DefaultLanguageHighlighterColors.OPERATION_SIGN
    )

    val NUMBER = TextAttributesKey.createTextAttributesKey(
        "NUMBER",
        DefaultLanguageHighlighterColors.NUMBER
    )
}