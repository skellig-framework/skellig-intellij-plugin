package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKeywordProvider
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes

class SkelligTestStepSyntaxHighlighter(private val myKeywordProvider: SkelligTestStepKeywordProvider) : SyntaxHighlighterBase() {
    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        init {
            SkelligTestStepTokenTypes.KEYWORDS.types.forEach { ATTRIBUTES[it] = SkelligTestStepHighlighter.KEYWORD }
            SkelligTestStepTokenTypes.BRACKETS.types.forEach { ATTRIBUTES[it] = SkelligTestStepHighlighter.BRACKETS }
            ATTRIBUTES[SkelligTestStepTokenTypes.COMMENT] = SkelligTestStepHighlighter.COMMENT
            ATTRIBUTES[SkelligTestStepTokenTypes.TEXT] = SkelligTestStepHighlighter.TEXT
            ATTRIBUTES[SkelligTestStepTokenTypes.STRING_TEXT] = SkelligTestStepHighlighter.STRING
            ATTRIBUTES[SkelligTestStepTokenTypes.PROPERTY] = SkelligTestStepHighlighter.PROPERTY
            ATTRIBUTES[SkelligTestStepTokenTypes.PARAMETER] = SkelligTestStepHighlighter.PARAMETER
        }
    }

    override fun getHighlightingLexer(): Lexer {
        return SkelligTestStepLexer(myKeywordProvider)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[tokenType])
    }
}