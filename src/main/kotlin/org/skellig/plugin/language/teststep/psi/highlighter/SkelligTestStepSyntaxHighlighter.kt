package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepSyntaxHighlighter() : SyntaxHighlighterBase() {
    companion object {
        val COMMENT = arrayOf(SkelligTestStepHighlighter.COMMENT)
        val KEYWORD = arrayOf(SkelligTestStepHighlighter.KEYWORD)
        val BRACKETS = arrayOf(SkelligTestStepHighlighter.BRACKETS)
        val STRING = arrayOf(SkelligTestStepHighlighter.STRING)
        val OPERATION = arrayOf(SkelligTestStepHighlighter.OPERATION)
        val NUMBER = arrayOf(SkelligTestStepHighlighter.NUMBER)
        val TEXT = arrayOf(TextAttributesKey.createTextAttributesKey("TEST_STEP_TEXT", HighlighterColors.TEXT))
    }

    override fun getHighlightingLexer(): Lexer {
        return SkelligTestStepLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            SkelligTestStepTypes.NAME -> KEYWORD
            SkelligTestStepTypes.VALUES -> KEYWORD
            SkelligTestStepTypes.VALIDATE -> KEYWORD
            SkelligTestStepTypes.BODY -> KEYWORD
            SkelligTestStepTypes.MESSAGE -> KEYWORD
            SkelligTestStepTypes.REQUEST -> KEYWORD
            SkelligTestStepTypes.WHERE -> KEYWORD
            SkelligTestStepTypes.PAYLOAD -> KEYWORD

            SkelligTestStepTypes.KEY_SYMBOLS -> TEXT

            SkelligTestStepTypes.STRING -> STRING

            SkelligTestStepTypes.COMMENT -> COMMENT

            SkelligTestStepTypes.VALUE_SYMBOLS -> OPERATION
            SkelligTestStepTypes.LESSER_EQUAL -> OPERATION
            SkelligTestStepTypes.EQUAL -> OPERATION
            SkelligTestStepTypes.GREATER_EQUAL -> OPERATION
            SkelligTestStepTypes.VALUE_ASSIGN -> OPERATION
            SkelligTestStepTypes.NOT_EQUAL -> OPERATION
            SkelligTestStepTypes.COMMA -> OPERATION
            SkelligTestStepTypes.DOT -> OPERATION

            SkelligTestStepTypes.REFERENCE_BRACKET -> BRACKETS
            SkelligTestStepTypes.OBJECT_L_BRACKET -> BRACKETS
            SkelligTestStepTypes.OBJECT_R_BRACKET -> BRACKETS
            SkelligTestStepTypes.ARRAY_L_BRACKET -> BRACKETS
            SkelligTestStepTypes.ARRAY_R_BRACKET -> BRACKETS
            SkelligTestStepTypes.FUNCTION_L_BRACKET -> BRACKETS
            SkelligTestStepTypes.FUNCTION_R_BRACKET -> BRACKETS

            SkelligTestStepTypes.INT -> NUMBER
            SkelligTestStepTypes.FLOAT -> NUMBER
            else -> emptyArray()
        }
    }
}