package org.jetbrains.plugins.skellig.teststep.psi.reference

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStep

class SkelligTestStepDefinition(element: PsiElement) : AbstractStepDefinition(element) {

    override val variableNames: List<String>
        get() {
            return emptyList()
        }

    override fun getCucumberRegexFromElement(element: PsiElement?): String {
        val stepName = (element as SkelligTestStep).stepName
        if (stepName.isNotEmpty() && (stepName.startsWith("'") || stepName.startsWith("\""))) {
            return stepName.substring(1, stepName.length - 1)
        }
        return stepName
    }
}