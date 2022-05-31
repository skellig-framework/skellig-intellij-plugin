package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.IElementType

interface SkelligTestStepKeywordProvider {
    fun getAllKeywords(): Collection<String?>
    fun getTokenType(keyword: String?): IElementType?
    fun getBaseKeyword(keyword: String): String
    fun isSpaceRequiredAfterKeyword(keyword: String?): Boolean
    fun isStepKeyword(keyword: String?): Boolean
    fun getKeywordsTable(): SkelligTestStepKeywordTable
}