// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.IElementType

class PlainSkelligTestStepKeywordProvider : SkelligTestStepKeywordProvider {

    companion object {
        var DEFAULT_KEYWORD_TABLE = SkelligTestStepKeywordTable()
        var DEFAULT_KEYWORDS: MutableMap<String?, IElementType> = HashMap()
        private val ourKeywordsWithNoSpaceAfter: MutableSet<String?> = HashSet()

        init {
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.NAME, "Name")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.ID, "id")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.IF, "if")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.TEMPLATE, "template")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.JSON, "json")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.CSV, "csv")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.VARIABLES, "variables")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.MESSAGE, "message")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.REQUEST, "request")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.BODY, "body")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.PAYLOAD, "payload")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.VALIDATE, "validate")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.ASSERT, "assert")
            DEFAULT_KEYWORD_TABLE.put(SkelligTestStepTokenTypes.FROMTEST, "fromTest")
            DEFAULT_KEYWORD_TABLE.putAllKeywordsInto(DEFAULT_KEYWORDS)
        }
    }

    override fun getAllKeywords(): Collection<String?> {
        return DEFAULT_KEYWORDS.keys
    }

    override fun getTokenType(keyword: String?): IElementType? {
        return DEFAULT_KEYWORDS[keyword]
    }

    override fun getBaseKeyword(keyword: String): String {
        return keyword
    }

    override fun isSpaceRequiredAfterKeyword(keyword: String?): Boolean {
        return !ourKeywordsWithNoSpaceAfter.contains(keyword)
    }

    override fun isStepKeyword(keyword: String?): Boolean {
        return DEFAULT_KEYWORDS[keyword] === SkelligTestStepTokenTypes.NAME
    }

    override fun getKeywordsTable(): SkelligTestStepKeywordTable {
        return DEFAULT_KEYWORD_TABLE
    }
}