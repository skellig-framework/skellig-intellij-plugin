// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.cucumber.CucumberElementFactory

/**
 * @author Roman.Chernyatchik
 * @date Sep 5, 2009
 */
object GherkinElementFactory {
    private val LOG = Logger.getInstance(GherkinElementFactory::class.java.name)
    fun createFeatureFromText(project: Project?, text: String): GherkinFeature? {
        val list: Array<PsiElement> = getTopLevelElements(project, text)
        for (psiElement in list) {
            if (psiElement is GherkinFeature) {
                return psiElement
            }
        }
        LOG.error("Failed to create Feature from text:\n$text")
        return null
    }

    fun createScenarioFromText(project: Project?, language: String, text: String): GherkinStepsHolder {
        val provider: GherkinKeywordProvider = PlainGherkinKeywordProvider()
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
        return CucumberElementFactory.createTempPsiFile(project, text).getChildren()
    }
}