package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType

class PlainSkelligKeywordProvider : SkelligKeywordProvider {
    companion object {
        var DEFAULT_KEYWORD_TABLE = SkelligKeywordTable()
        var DEFAULT_KEYWORDS: MutableMap<String?, IElementType> = HashMap()
        private val ourKeywordsWithNoSpaceAfter: MutableSet<String?> = HashSet()

        init {
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.FEATURE_KEYWORD, "Feature")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.BACKGROUND_KEYWORD, "Background")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.SCENARIO_KEYWORD, "Scenario")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.RULE_KEYWORD, "Rule")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.SCENARIO_KEYWORD, "Example")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.EXAMPLES_KEYWORD, "Examples")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.EXAMPLES_KEYWORD, "Scenarios")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "Given")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "When")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "Then")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "And")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "But")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "*")
            DEFAULT_KEYWORD_TABLE.put(SkelligTokenTypes.STEP_KEYWORD, "Lorsqu'")
            ourKeywordsWithNoSpaceAfter.add("Lorsqu'")
            DEFAULT_KEYWORD_TABLE.putAllKeywordsInto(DEFAULT_KEYWORDS)
        }
    }

    override fun getAllKeywords(language: String?): Collection<String?>? {
        return DEFAULT_KEYWORDS.keys
    }

    override fun getTokenType(language: String?, keyword: String?): IElementType? {
        return DEFAULT_KEYWORDS[keyword]
    }

    override fun getBaseKeyword(language: String?, keyword: String): String {
        return keyword
    }

    override fun isSpaceRequiredAfterKeyword(language: String?, keyword: String?): Boolean {
        return !ourKeywordsWithNoSpaceAfter.contains(keyword)
    }

    override fun isStepKeyword(keyword: String?): Boolean {
        return DEFAULT_KEYWORDS[keyword] === SkelligTokenTypes.Companion.STEP_KEYWORD
    }

    override fun getKeywordsTable(language: String?): SkelligKeywordTable {
        return DEFAULT_KEYWORD_TABLE
    }
}