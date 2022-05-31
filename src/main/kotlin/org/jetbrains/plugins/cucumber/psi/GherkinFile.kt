package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.PsiFile

interface GherkinFile : PsiFile {
    fun getStepKeywords(): List<String>
    fun getLocaleLanguage(): String
    fun getFeatures(): Array<GherkinFeature?>
}