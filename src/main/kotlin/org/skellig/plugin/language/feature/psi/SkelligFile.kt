package org.skellig.plugin.language.feature.psi

import com.intellij.psi.PsiFile

interface SkelligFile : PsiFile {
    fun getStepKeywords(): List<String>
    fun getLocaleLanguage(): String
    fun getFeatures(): Array<SkelligFeature?>
}