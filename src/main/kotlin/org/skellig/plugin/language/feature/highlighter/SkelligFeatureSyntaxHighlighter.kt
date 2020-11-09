package org.skellig.plugin.language.feature.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.feature.SkelligFeatureLexerAdapter
import org.skellig.plugin.language.feature.psi.SkelligFeatureTypes


class SkelligFeatureSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val TAG: TextAttributesKey = createTextAttributesKey("FEATURE_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val KEYWORD: TextAttributesKey = createTextAttributesKey("FEATURE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val FEATURE_PARAMETER: TextAttributesKey = createTextAttributesKey("FEATURE_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER)
        val PARAMETER_SEPARATOR: TextAttributesKey = createTextAttributesKey("FEATURE_PARAMETER_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val FEATURE_TEXT: TextAttributesKey = createTextAttributesKey("FEATURE_TEXT", DefaultLanguageHighlighterColors.IDENTIFIER)
        val STRING: TextAttributesKey = createTextAttributesKey("FEATURE_STRING", DefaultLanguageHighlighterColors.STRING)
        val COMMENT: TextAttributesKey = createTextAttributesKey("FEATURE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val SKELLIG_BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("FEATURE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
    }

    private val BAD_CHAR_KEYS = arrayOf(SKELLIG_BAD_CHARACTER)
    private val TAG_KEYS = arrayOf(TAG)
    private val TEXT_KEYS = arrayOf(FEATURE_TEXT)
    private val STRING_KEYS = arrayOf(STRING)
    private val COMMENT_KEYS = arrayOf(COMMENT)
    private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    private val PARAMETER_SEPARATOR_KEYS = arrayOf(PARAMETER_SEPARATOR)
    private val PARAMETER_KEYS = arrayOf(FEATURE_PARAMETER)
    private val KEYWORDS_KEYS =arrayOf(KEYWORD)
    private val KEYWORDS =
            setOf(SkelligFeatureTypes.NAME,
                    SkelligFeatureTypes.TEST,
                    SkelligFeatureTypes.DATA)

    @NotNull
    override fun getHighlightingLexer(): Lexer {
        return SkelligFeatureLexerAdapter()
    }

    @NotNull
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return if (tokenType == SkelligFeatureTypes.TAG_REGEX) {
            TAG_KEYS
        } else if (KEYWORDS.contains(tokenType)) {
            KEYWORDS_KEYS
        } else if (tokenType == SkelligFeatureTypes.PARAM_REGEX) {
            PARAMETER_KEYS
        } else if (tokenType == SkelligFeatureTypes.SYMBOLS ||
                tokenType == SkelligFeatureTypes.COLON) {
            TEXT_KEYS
        } else if (tokenType == SkelligFeatureTypes.PARAM_SEPARATOR) {
            PARAMETER_SEPARATOR_KEYS
        } else if (tokenType == SkelligFeatureTypes.COMMENT) {
            COMMENT_KEYS
        } else if (tokenType == SkelligFeatureTypes.STRING) {
            STRING_KEYS
        } else if (tokenType == TokenType.BAD_CHARACTER) {
            BAD_CHAR_KEYS
        } else {
            EMPTY_KEYS
        }
    }
}