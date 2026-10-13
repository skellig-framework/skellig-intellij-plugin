package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType

class PlainSkelligKeywordProvider : SkelligKeywordProvider {
    companion object {
        var defaultKeywordTable = SkelligKeywordTable()
        var defaultKeywords: MutableMap<String?, IElementType> = HashMap()
        private val ourKeywordsWithNoSpaceAfter: MutableSet<String?> = HashSet()

        init {
            defaultKeywordTable.put(SkelligTokenTypes.FEATURE_KEYWORD, "Feature")
            defaultKeywordTable.put(SkelligTokenTypes.BACKGROUND_KEYWORD, "Background")
            defaultKeywordTable.put(SkelligTokenTypes.SCENARIO_KEYWORD, "Scenario")
            defaultKeywordTable.put(SkelligTokenTypes.RULE_KEYWORD, "Rule")
            defaultKeywordTable.put(SkelligTokenTypes.SCENARIO_KEYWORD, "Example")
            defaultKeywordTable.put(SkelligTokenTypes.EXAMPLES_KEYWORD, "Examples")
            defaultKeywordTable.put(SkelligTokenTypes.EXAMPLES_KEYWORD, "Scenarios")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "Given")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "When")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "Then")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "And")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "But")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "*")
            defaultKeywordTable.put(SkelligTokenTypes.STEP_KEYWORD, "Lorsqu'")
            ourKeywordsWithNoSpaceAfter.add("Lorsqu'")
            defaultKeywordTable.putAllKeywordsInto(defaultKeywords)
        }
    }

    override fun getAllKeywords(language: String?): Collection<String?>? = defaultKeywords.keys

    override fun getTokenType(
        language: String?,
        keyword: String?,
    ): IElementType? = defaultKeywords[keyword]

    override fun getBaseKeyword(
        language: String?,
        keyword: String,
    ): String = keyword

    override fun isSpaceRequiredAfterKeyword(
        language: String?,
        keyword: String?,
    ): Boolean = !ourKeywordsWithNoSpaceAfter.contains(keyword)

    override fun isStepKeyword(keyword: String?): Boolean = defaultKeywords[keyword] === SkelligTokenTypes.Companion.STEP_KEYWORD

    override fun getKeywordsTable(language: String?): SkelligKeywordTable = defaultKeywordTable
}
