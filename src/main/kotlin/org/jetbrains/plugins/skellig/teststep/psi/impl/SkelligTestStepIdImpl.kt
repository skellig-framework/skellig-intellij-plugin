package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementTypes
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

open class SkelligTestStepIdImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node) {

    override val property: SkelligTestStepProperty?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.ID)?.psi as SkelligTestStepProperty?
        }

    override fun toString(): String {
        return "SkelligTestStepIdImpl: $elementText"
    }

}