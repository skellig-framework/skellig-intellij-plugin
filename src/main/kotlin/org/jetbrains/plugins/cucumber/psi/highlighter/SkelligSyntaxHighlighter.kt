// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.jetbrains.plugins.cucumber.psi.GherkinKeywordProvider
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes
import org.jetbrains.plugins.cucumber.psi.SkelligLexer

class SkelligSyntaxHighlighter(private val myKeywordProvider: GherkinKeywordProvider) : SyntaxHighlighterBase() {
    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        init {
            GherkinTokenTypes.KEYWORDS.types.forEach { ATTRIBUTES[it] = SkelligHighlighter.KEYWORD }
            ATTRIBUTES[GherkinTokenTypes.COMMENT] = SkelligHighlighter.COMMENT
            ATTRIBUTES[GherkinTokenTypes.TEXT] = SkelligHighlighter.TEXT
            ATTRIBUTES[GherkinTokenTypes.TAG] = SkelligHighlighter.TAG
            ATTRIBUTES[GherkinTokenTypes.PYSTRING] = SkelligHighlighter.PYSTRING
            ATTRIBUTES[GherkinTokenTypes.PYSTRING_TEXT] = SkelligHighlighter.PYSTRING
            ATTRIBUTES[GherkinTokenTypes.TABLE_CELL] = SkelligHighlighter.TABLE_CELL
            ATTRIBUTES[GherkinTokenTypes.PIPE] = SkelligHighlighter.PIPE
        }
    }

    override fun getHighlightingLexer(): Lexer {
        return SkelligLexer(myKeywordProvider)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[tokenType])
    }
}