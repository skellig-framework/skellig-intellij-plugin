package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.psi.PsiElement
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.teststep.psi.SkelligTestStepExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression

class SkelligTestStepDefinition(element: PsiElement) : AbstractStepDefinition(element) {

    override val variableNames: List<String>
        get() {
            return emptyList()
        }

    override fun getCucumberRegexFromElement(element: PsiElement?): String {
        val stepName = (element as SkelligTestStepTestStepNameExpression).testStepName.text
        if (stepName.isNotEmpty() && (stepName.startsWith("'") || stepName.startsWith("\""))) {
            return stepName.substring(1, stepName.length - 1)
        }
        return stepName
    }
}