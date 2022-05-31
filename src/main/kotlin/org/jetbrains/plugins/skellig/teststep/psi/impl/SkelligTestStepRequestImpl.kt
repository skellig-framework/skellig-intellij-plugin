package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

interface SkelligTestStepRequest : PsiElement {
    val value: PsiElement?
}

class SkelligTestStepRequestImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepRequest{

    override fun toString(): String {
        return "SkelligTestStepRequest: $elementText"
    }
}