package org.skellig.plugin.language.feature.steps.reference.java

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

class SkelligJavaTestStepDefinition(element: PsiElement) : AbstractStepDefinition(element) {

    override val variableNames: List<String>
        get() {
            return emptyList()
        }

    override fun getRegexFromElement(element: PsiElement?): String? {
        if (element == null) {
            return null
        }

        if (element !is PsiAnnotation) {
            return null
        }

        return SkelligUtil.getTestStepName(element)
    }
}