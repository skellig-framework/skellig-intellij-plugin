package org.skellig.plugin.language.feature.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.feature.psi.SkelligKeywordProvider
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes
import org.skellig.plugin.language.feature.psi.SkelligLexer

class SkelligSyntaxHighlighter(private val myKeywordProvider: SkelligKeywordProvider) : SyntaxHighlighterBase() {
    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        init {
            SkelligTokenTypes.KEYWORDS.types.forEach { ATTRIBUTES[it] = SkelligHighlighter.KEYWORD }
            ATTRIBUTES[SkelligTokenTypes.COMMENT] = SkelligHighlighter.COMMENT
            ATTRIBUTES[SkelligTokenTypes.TEXT] = SkelligHighlighter.TEXT
            ATTRIBUTES[SkelligTokenTypes.TAG] = SkelligHighlighter.TAG
            ATTRIBUTES[SkelligTokenTypes.PYSTRING] = SkelligHighlighter.PYSTRING
            ATTRIBUTES[SkelligTokenTypes.PYSTRING_TEXT] = SkelligHighlighter.PYSTRING
            ATTRIBUTES[SkelligTokenTypes.TABLE_CELL] = SkelligHighlighter.TABLE_CELL
            ATTRIBUTES[SkelligTokenTypes.PIPE] = SkelligHighlighter.PIPE
        }
    }

    override fun getHighlightingLexer(): Lexer {
        return SkelligLexer(myKeywordProvider)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[tokenType])
    }
}