package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement


object SkelligElementFactory {
    private val LOG = Logger.getInstance(SkelligElementFactory::class.java.name)
    fun createFeatureFromText(project: Project?, text: String): SkelligFeature? {
        val list: Array<PsiElement> = getTopLevelElements(project, text)
        for (psiElement in list) {
            if (psiElement is SkelligFeature) {
                return psiElement
            }
        }
        LOG.error("Failed to create Feature from text:\n$text")
        return null
    }

    fun createScenarioFromText(project: Project?, language: String, text: String): SkelligStepsHolder {
        val provider: SkelligKeywordProvider = PlainSkelligKeywordProvider()
        val keywordsTable = provider.getKeywordsTable(language)
        val featureText = """
               # language: $language
               ${keywordsTable.getFeatureSectionKeyword()}: Dummy
               $text
               """.trimIndent()
        val feature = createFeatureFromText(project, featureText)
        return feature!!.scenarios!![0]
    }

    fun getTopLevelElements(project: Project?, text: String): Array<PsiElement> {
        return org.skellig.plugin.language.feature.SkelligElementFactory.createTempPsiFile(project, text).getChildren()
    }
}