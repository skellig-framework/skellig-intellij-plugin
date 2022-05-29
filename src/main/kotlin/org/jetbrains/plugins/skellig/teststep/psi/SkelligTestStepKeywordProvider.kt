// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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