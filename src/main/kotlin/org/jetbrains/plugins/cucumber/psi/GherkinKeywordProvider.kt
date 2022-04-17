// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.tree.IElementType

interface GherkinKeywordProvider {
    fun getAllKeywords(language: String?): Collection<String?>?
    fun getTokenType(language: String?, keyword: String?): IElementType?
    fun getBaseKeyword(language: String?, keyword: String): String
    fun isSpaceRequiredAfterKeyword(language: String?, keyword: String?): Boolean
    fun isStepKeyword(keyword: String?): Boolean
    fun getKeywordsTable(language: String?): GherkinKeywordTable
}