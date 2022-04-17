// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import org.jetbrains.plugins.cucumber.psi.*

class GherkinFileImpl(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SkelligLanguage.INSTANCE), GherkinFile {

    override fun getFileType(): FileType = SkelligFileType.INSTANCE

    override fun getStepKeywords(): List<String> {
        val provider: GherkinKeywordProvider = PlainGherkinKeywordProvider()
        val result: MutableList<String> = ArrayList()

        // find language comment
        val language = getLocaleLanguage()

        // step keywords
        val table: GherkinKeywordTable = provider.getKeywordsTable(language)
        result.addAll(table.getStepKeywords()!!)
        return result
    }

    override fun getLocaleLanguage(): String {
        val node: ASTNode = getNode()
        var child = node.firstChildNode
        while (child != null) {
            if (child.elementType === GherkinTokenTypes.Companion.COMMENT) {
                val text = child.text.substring(1).trim { it <= ' ' }
                val lang: String? = SkelligLexer.Companion.fetchLocationLanguage(text)
                if (lang != null) {
                    return lang
                }
            } else {
                if (child.elementType !== TokenType.WHITE_SPACE) {
                    break
                }
            }
            child = child.treeNext
        }
        return defaultLocale
    }

    override fun getFeatures(): Array<GherkinFeature?> = findChildrenByClass(GherkinFeature::class.java)

    override fun toString(): String {
        return "GherkinFile:" + getName()
    }

    override fun findElementAt(offset: Int): PsiElement? {
        var result: PsiElement? = super.findElementAt(offset)
        if (result == null && offset == getTextLength()) {
            val last: PsiElement? = getLastChild()
            result = if (last != null) last.getLastChild() else last
        }
        return result
    }

    companion object {
        val defaultLocale: String
            get() = "en"
    }
}