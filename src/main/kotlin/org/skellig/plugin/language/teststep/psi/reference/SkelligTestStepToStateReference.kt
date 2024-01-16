package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression


class SkelligTestStepToStateReference(stepParameter: SkelligTestStepFunctionExpression) : SkelligTestStepSimpleReference(stepParameter) {
    val element: SkelligTestStepFunctionExpression
        get() = super.getElement() as SkelligTestStepFunctionExpression

    override fun resolve(): PsiElement? {
        val parameterText = element.functionName.text
        if(parameterText == "get") {
            val keyName = element.argList[0].text
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStepTestStepNameExpression::class.java) ?: return null
            val pairs = PsiTreeUtil.getChildrenOfTypeAsList(testStep, SkelligTestStepPair::class.java)
                .find { it.key.text == "state" }
            pairs?.let { values ->
                values.map?.let {
                    for (pair in it.pairList) {
                        val text = pair.key.text
                        if (text == keyName) {
                            return pair
                        }
                    }
                }
            }
        }
        return null
    }
}
