package org.jetbrains.plugins.cucumber.psi.highlighter

import org.jetbrains.annotations.NonNls
import com.intellij.openapi.editor.colors.TextAttributesKey
import org.jetbrains.plugins.cucumber.psi.highlighter.SkelligHighlighter
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors

/**
 * @author Roman.Chernyatchik
 */
object SkelligHighlighter {

    @NonNls
    val COMMENT_ID = "GHERKIN_COMMENT"
    val COMMENT = TextAttributesKey.createTextAttributesKey(
        COMMENT_ID,
        DefaultLanguageHighlighterColors.DOC_COMMENT
    )

    @NonNls
    val KEYWORD_ID = "GHERKIN_KEYWORD"
    val KEYWORD = TextAttributesKey.createTextAttributesKey(
        KEYWORD_ID,
        DefaultLanguageHighlighterColors.KEYWORD
    )

    @NonNls
    val GHERKIN_OUTLINE_PARAMETER_SUBSTITUTION_ID = "GHERKIN_OUTLINE_PARAMETER_SUBSTITUTION"
    val OUTLINE_PARAMETER_SUBSTITUTION = TextAttributesKey.createTextAttributesKey(
        GHERKIN_OUTLINE_PARAMETER_SUBSTITUTION_ID,
        DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )

    @NonNls
    val GHERKIN_TABLE_HEADER_CELL_ID = "GHERKIN_TABLE_HEADER_CELL"
    val TABLE_HEADER_CELL = TextAttributesKey.createTextAttributesKey(
        GHERKIN_TABLE_HEADER_CELL_ID,
        OUTLINE_PARAMETER_SUBSTITUTION
    )

    @NonNls
    val GHERKIN_TAG_ID = "GHERKIN_TAG"
    val TAG = TextAttributesKey.createTextAttributesKey(
        GHERKIN_TAG_ID,
        DefaultLanguageHighlighterColors.METADATA
    )

    @NonNls
    val GHERKIN_REGEXP_PARAMETER_ID = "GHERKIN_REGEXP_PARAMETER"
    val REGEXP_PARAMETER = TextAttributesKey.createTextAttributesKey(
        GHERKIN_REGEXP_PARAMETER_ID,
        DefaultLanguageHighlighterColors.PARAMETER
    )

    @NonNls
    val GHERKIN_TABLE_CELL_ID = "GHERKIN_TABLE_CELL"
    val TABLE_CELL = TextAttributesKey.createTextAttributesKey(
        GHERKIN_TABLE_CELL_ID,
        REGEXP_PARAMETER
    )

    @NonNls
    val GHERKIN_PYSTRING_ID = "GHERKIN_PYSTRING"
    val PYSTRING = TextAttributesKey.createTextAttributesKey(
        GHERKIN_PYSTRING_ID,
        DefaultLanguageHighlighterColors.STRING
    )
    val TEXT = TextAttributesKey.createTextAttributesKey("GHERKIN_TEXT", HighlighterColors.TEXT)
    val PIPE = TextAttributesKey.createTextAttributesKey("GHERKIN_TABLE_PIPE", KEYWORD)
}