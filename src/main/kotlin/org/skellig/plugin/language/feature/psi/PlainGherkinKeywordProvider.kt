package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType

class PlainGherkinKeywordProvider : GherkinKeywordProvider {
    companion object {
        var DEFAULT_KEYWORD_TABLE = GherkinKeywordTable()
        var DEFAULT_KEYWORDS: MutableMap<String?, IElementType> = HashMap()
        private val ourKeywordsWithNoSpaceAfter: MutableSet<String?> = HashSet()

        init {
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.FEATURE_KEYWORD, "Feature")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.BACKGROUND_KEYWORD, "Background")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.SCENARIO_KEYWORD, "Scenario")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.RULE_KEYWORD, "Rule")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.SCENARIO_KEYWORD, "Example")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.SCENARIO_OUTLINE_KEYWORD, "Scenario Outline")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.EXAMPLES_KEYWORD, "Examples")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.EXAMPLES_KEYWORD, "Scenarios")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "Given")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "When")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "Then")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "And")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "But")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "*")
            DEFAULT_KEYWORD_TABLE.put(GherkinTokenTypes.STEP_KEYWORD, "Lorsqu'")
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
        return DEFAULT_KEYWORDS[keyword] === GherkinTokenTypes.Companion.STEP_KEYWORD
    }

    override fun getKeywordsTable(language: String?): GherkinKeywordTable {
        return DEFAULT_KEYWORD_TABLE
    }
}