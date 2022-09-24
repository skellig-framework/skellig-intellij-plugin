package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementTypes

interface SkelligTestStepRequest : PsiElement {
    val value: PsiElement?
}

class SkelligTestStepRequestImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepRequest {

    override val property: PsiElement?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.REQUEST)?.psi
        }

    override fun toString(): String {
        return "SkelligTestStepRequest: $elementText"
    }
}