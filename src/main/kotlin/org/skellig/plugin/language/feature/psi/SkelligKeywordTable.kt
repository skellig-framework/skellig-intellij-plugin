package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType


class SkelligKeywordTable {
    private val myType2KeywordsTable: MutableMap<IElementType, MutableCollection<String>> = HashMap()
    fun putAllKeywordsInto(target: MutableMap<String?, IElementType>) {
        for (type in getTypes()) {
            val keywords: Collection<String>? = getKeywords(type)
            if (keywords != null) {
                for (keyword in keywords) {
                    target[keyword] = type
                }
            }
        }
    }

    fun put(type: IElementType, keyword: String) {
        if (SkelligTokenTypes.Companion.KEYWORDS.contains(type)) {
            var keywords = getKeywords(type)
            if (keywords == null) {
                keywords = ArrayList(1)
                myType2KeywordsTable[type] = keywords
            }
            keywords.add(keyword)
        }
    }

    fun getStepKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.STEP_KEYWORD)!!
    }

    fun getScenarioKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.SCENARIO_KEYWORD)
    }

    fun getScenarioLikeKeywords(): Collection<String> {
        val keywords: MutableSet<String> = HashSet()
        val scenarios: Collection<String> = getKeywords(SkelligTokenTypes.Companion.SCENARIO_KEYWORD)!!
        keywords.addAll(scenarios)
        return keywords
    }

    fun getRuleKeywords(): Collection<String> {
        val result: Collection<String>? = getKeywords(SkelligTokenTypes.Companion.RULE_KEYWORD)
        return result ?: emptyList()
    }

    fun getScenarioOutlineKeyword(): String {
        return getScenarioOutlineKeywords()!!.iterator().next()
    }

    fun getScenarioOutlineKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.SCENARIO_KEYWORD)!!
    }

    fun getBackgroundKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.BACKGROUND_KEYWORD)!!
    }

    fun getExampleSectionKeyword(): String {
        return getExampleSectionKeywords()!!.iterator().next()
    }

    fun getExampleSectionKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.EXAMPLES_KEYWORD)!!
    }

    fun getFeatureSectionKeyword(): String {
        return getFeaturesSectionKeywords()!!.iterator().next()
    }

    fun getFeaturesSectionKeywords(): Collection<String>? {
        return getKeywords(SkelligTokenTypes.Companion.FEATURE_KEYWORD)!!
    }

    fun getTypes(): Collection<IElementType> {
        return myType2KeywordsTable.keys
    }

    fun getKeywords(type: IElementType): MutableCollection<String>? {
        return myType2KeywordsTable[type]
    }

    fun tableContainsKeyword(type: SkelligElementType, keyword: String): Boolean {
        val alreadyKnownKeywords: Collection<String>? = getKeywords(type)
        return null != alreadyKnownKeywords && alreadyKnownKeywords.contains(keyword)
    }

    init {
        for (type in SkelligTokenTypes.Companion.KEYWORDS.getTypes()) {
            myType2KeywordsTable[type] = ArrayList()
        }
    }
}