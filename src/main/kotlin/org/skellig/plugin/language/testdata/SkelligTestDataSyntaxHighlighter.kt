package org.skellig.plugin.language.testdata

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes


class SkelligTestDataSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val SEPARATOR: TextAttributesKey = createTextAttributesKey("SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEYWORD: TextAttributesKey = createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val DATA: TextAttributesKey = createTextAttributesKey("DATA", DefaultLanguageHighlighterColors.IDENTIFIER)
        val STRING: TextAttributesKey = createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER: TextAttributesKey = createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT: TextAttributesKey = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val SKELLIG_BRACKETS: TextAttributesKey = createTextAttributesKey("SKELLIG_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val SKELLIG_BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("SKELLIG_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
    }

    private val BAD_CHAR_KEYS = arrayOf(SKELLIG_BAD_CHARACTER)
    private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
    private val KEYWORD_KEYS = arrayOf(KEYWORD)
    private val DATA_KEYS = arrayOf(DATA)
    private val STRING_KEYS = arrayOf(STRING)
    private val NUMBER_KEYS = arrayOf(NUMBER)
    private val COMMENT_KEYS = arrayOf(COMMENT)
    private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    private val BRACKETS_KEYS = arrayOf(SKELLIG_BRACKETS)
    private val BRACKETS =
            setOf(SkelligTestDataTypes.PARAMETEROPENBRACKET,
                    SkelligTestDataTypes.OBJECTOPENBRACKET,
                    SkelligTestDataTypes.OBJECTCLOSEBRACKET,
                    SkelligTestDataTypes.ARRAYOPENBRACKET,
                    SkelligTestDataTypes.ARRAYCLOSEBRACKET,
                    SkelligTestDataTypes.FUNCTIONOPENBRACKET,
                    SkelligTestDataTypes.FUNCTIONCLOSEBRACKET)
    private val KEYWORDS =
            setOf(SkelligTestDataTypes.IF,
                    SkelligTestDataTypes.NAME,
                    SkelligTestDataTypes.FROMTEST,
                    SkelligTestDataTypes.ID,
                    SkelligTestDataTypes.JSON,
                    SkelligTestDataTypes.TEMPLATE,
                    SkelligTestDataTypes.ASSERT,
                    SkelligTestDataTypes.VALIDATE,
                    SkelligTestDataTypes.VARIABLES,
                    SkelligTestDataTypes.MESSAGE,
                    SkelligTestDataTypes.CSV,
                    SkelligTestDataTypes.REQUEST,
                    SkelligTestDataTypes.PAYLOAD,
                    SkelligTestDataTypes.BODY)

    @NotNull
    override fun getHighlightingLexer(): Lexer {
        return SkelligTestDataLexerAdapter()
    }

    @NotNull
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return if (tokenType == SkelligTestDataTypes.SEPARATOR) {
            SEPARATOR_KEYS
        } else if (KEYWORDS.contains(tokenType)) {
            KEYWORD_KEYS
        } else if (BRACKETS.contains(tokenType)) {
            BRACKETS_KEYS
        } else if (tokenType == SkelligTestDataTypes.SYMBOLS) {
            DATA_KEYS
        } else if (tokenType == SkelligTestDataTypes.COMMENT) {
            COMMENT_KEYS
        } else if (tokenType == SkelligTestDataTypes.STRING) {
            STRING_KEYS
        } else if (tokenType == SkelligTestDataTypes.NUMBER) {
            NUMBER_KEYS
        } else if (tokenType == TokenType.BAD_CHARACTER) {
            BAD_CHAR_KEYS
        } else {
            EMPTY_KEYS
        }
    }
}