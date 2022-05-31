package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepVariables : PsiElement

class SkelligTestStepVariablesImpl(node: ASTNode) : SkelligTestStepObjectImpl(node), SkelligTestStepVariables {

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

    override fun toString(): String {
        return "SkelligTestStepVariables: $elementText"
    }
}