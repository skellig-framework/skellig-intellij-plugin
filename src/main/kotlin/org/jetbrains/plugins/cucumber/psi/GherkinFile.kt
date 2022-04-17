// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.PsiFile

interface GherkinFile : PsiFile {
    fun getStepKeywords(): List<String>
    fun getLocaleLanguage(): String
    fun getFeatures(): Array<GherkinFeature?>
}