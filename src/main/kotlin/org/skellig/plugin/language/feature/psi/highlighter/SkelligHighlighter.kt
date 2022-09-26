package org.skellig.plugin.language.feature.psi.highlighter

import org.jetbrains.annotations.NonNls
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors

object SkelligHighlighter {

    @NonNls
    val COMMENT_ID = "SKELLIG_COMMENT"
    val COMMENT = TextAttributesKey.createTextAttributesKey(
        COMMENT_ID,
        DefaultLanguageHighlighterColors.DOC_COMMENT
    )

    @NonNls
    val KEYWORD_ID = "SKELLIG_KEYWORD"
    val KEYWORD = TextAttributesKey.createTextAttributesKey(
        KEYWORD_ID,
        DefaultLanguageHighlighterColors.KEYWORD
    )

    @NonNls
    val SKELLIG_OUTLINE_PARAMETER_SUBSTITUTION_ID = "SKELLIG_OUTLINE_PARAMETER_SUBSTITUTION"
    val OUTLINE_PARAMETER_SUBSTITUTION = TextAttributesKey.createTextAttributesKey(
        SKELLIG_OUTLINE_PARAMETER_SUBSTITUTION_ID,
        DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )

    @NonNls
    val SKELLIG_TABLE_HEADER_CELL_ID = "SKELLIG_TABLE_HEADER_CELL"
    val TABLE_HEADER_CELL = TextAttributesKey.createTextAttributesKey(
        SKELLIG_TABLE_HEADER_CELL_ID,
        OUTLINE_PARAMETER_SUBSTITUTION
    )

    @NonNls
    val SKELLIG_TAG_ID = "SKELLIG_TAG"
    val TAG = TextAttributesKey.createTextAttributesKey(
        SKELLIG_TAG_ID,
        DefaultLanguageHighlighterColors.METADATA
    )

    @NonNls
    val SKELLIG_REGEXP_PARAMETER_ID = "SKELLIG_REGEXP_PARAMETER"
    val REGEXP_PARAMETER = TextAttributesKey.createTextAttributesKey(
        SKELLIG_REGEXP_PARAMETER_ID,
        DefaultLanguageHighlighterColors.PARAMETER
    )

    @NonNls
    val SKELLIG_TABLE_CELL_ID = "SKELLIG_TABLE_CELL"
    val TABLE_CELL = TextAttributesKey.createTextAttributesKey(
        SKELLIG_TABLE_CELL_ID,
        REGEXP_PARAMETER
    )

    @NonNls
    val SKELLIG_PYSTRING_ID = "SKELLIG_PYSTRING"
    val PYSTRING = TextAttributesKey.createTextAttributesKey(
        SKELLIG_PYSTRING_ID,
        DefaultLanguageHighlighterColors.STRING
    )
    val TEXT = TextAttributesKey.createTextAttributesKey("SKELLIG_TEXT", HighlighterColors.TEXT)
    val PIPE = TextAttributesKey.createTextAttributesKey("SKELLIG_TABLE_PIPE", KEYWORD)
}