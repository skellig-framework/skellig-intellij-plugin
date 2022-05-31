package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

interface SkelligTestStepValidation {
    val value: PsiElement?
}

class SkelligTestStepValidationImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepValidation {

    override fun toString(): String {
        return "SkelligTestStepValidation: $elementText"
    }
}