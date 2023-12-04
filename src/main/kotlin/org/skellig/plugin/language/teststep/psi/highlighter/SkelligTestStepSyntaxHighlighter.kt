package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepSyntaxHighlighter() : SyntaxHighlighterBase() {
    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        init {
            ATTRIBUTES[SkelligTestStepTypes.NAME] = SkelligTestStepHighlighter.KEYWORD
            ATTRIBUTES[SkelligTestStepTypes.KEYWORDS] = SkelligTestStepHighlighter.KEYWORD
            ATTRIBUTES[SkelligTestStepTypes.KEY_SYMBOLS] = SkelligTestStepHighlighter.TEXT
            ATTRIBUTES[SkelligTestStepTypes.FUNCTION_NAME] = SkelligTestStepHighlighter.FUNCTION
            ATTRIBUTES[SkelligTestStepTypes.STRING] = SkelligTestStepHighlighter.STRING
            ATTRIBUTES[SkelligTestStepTypes.REFERENCE_KEY] = SkelligTestStepHighlighter.REFERENCE
            ATTRIBUTES[SkelligTestStepTypes.VALUE_SYMBOLS] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.LESSER_EQUAL] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.EQUAL] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.GREATER_EQUAL] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.VALUE_ASSIGN] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.NOT_EQUAL] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.COMMA] = SkelligTestStepHighlighter.OPERATION
            ATTRIBUTES[SkelligTestStepTypes.REFERENCE_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.OBJECT_L_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.OBJECT_R_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.ARRAY_L_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.ARRAY_R_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.FUNCTION_L_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.FUNCTION_R_BRACKET] = SkelligTestStepHighlighter.BRACKETS
            ATTRIBUTES[SkelligTestStepTypes.NUMBER] = SkelligTestStepHighlighter.NUMBER
        }
    }

    override fun getHighlightingLexer(): Lexer {
        return SkelligTestStepLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[tokenType])
    }
}