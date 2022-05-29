package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.IElementType

/**
 * @author Roman.Chernyatchik
 */
class SkelligTestStepKeywordTable {
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
        if (SkelligTestStepTokenTypes.KEYWORDS.contains(type)) {
            var keywords = getKeywords(type)
            if (keywords == null) {
                keywords = ArrayList(1)
                myType2KeywordsTable[type] = keywords
            }
            keywords.add(keyword)
        }
    }

    fun getTypes(): Collection<IElementType> {
        return myType2KeywordsTable.keys
    }

    fun getKeywords(type: IElementType): MutableCollection<String>? {
        return myType2KeywordsTable[type]
    }

    fun tableContainsKeyword(type: SkelligTestStepElementType, keyword: String): Boolean {
        val alreadyKnownKeywords: Collection<String>? = getKeywords(type)
        return null != alreadyKnownKeywords && alreadyKnownKeywords.contains(keyword)
    }

    init {
        for (type in SkelligTestStepTokenTypes.KEYWORDS.getTypes()) {
            myType2KeywordsTable[type] = ArrayList()
        }
    }
}