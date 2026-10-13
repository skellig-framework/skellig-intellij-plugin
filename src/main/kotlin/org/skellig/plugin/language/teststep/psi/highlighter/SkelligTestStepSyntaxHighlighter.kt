package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val COMMENT = arrayOf(SkelligTestStepHighlighter.COMMENT)
        val BRACKETS = arrayOf(SkelligTestStepHighlighter.BRACKETS)
        val STRING = arrayOf(SkelligTestStepHighlighter.STRING)
        val OPERATION = arrayOf(SkelligTestStepHighlighter.OPERATION)
        val NUMBER = arrayOf(SkelligTestStepHighlighter.NUMBER)
        val TEXT = arrayOf(TextAttributesKey.createTextAttributesKey("TEST_STEP_TEXT", HighlighterColors.TEXT))
    }

    override fun getHighlightingLexer(): Lexer = SkelligTestStepLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when (tokenType) {
            SkelligTestStepTypes.NAME -> TEXT
            SkelligTestStepTypes.VALUES -> TEXT
            SkelligTestStepTypes.VALIDATE -> TEXT
            SkelligTestStepTypes.BODY -> TEXT
            SkelligTestStepTypes.MESSAGE -> TEXT
            SkelligTestStepTypes.REQUEST -> TEXT
            SkelligTestStepTypes.WHERE -> TEXT
            SkelligTestStepTypes.PAYLOAD -> TEXT
            SkelligTestStepTypes.STATE -> TEXT

            SkelligTestStepTypes.ID -> TEXT
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
