package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementTypes

interface SkelligTestStepValidation {
    val value: PsiElement?
}

class SkelligTestStepValidationImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepValidation {

    override val property: PsiElement?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.VALIDATION)?.psi
        }

    override fun toString(): String {
        return "SkelligTestStepValidation: $elementText"
    }
}