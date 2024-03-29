package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType

interface SkelligKeywordProvider {
    fun getAllKeywords(language: String?): Collection<String?>?
    fun getTokenType(language: String?, keyword: String?): IElementType?
    fun getBaseKeyword(language: String?, keyword: String): String
    fun isSpaceRequiredAfterKeyword(language: String?, keyword: String?): Boolean
    fun isStepKeyword(keyword: String?): Boolean
    fun getKeywordsTable(language: String?): SkelligKeywordTable
}