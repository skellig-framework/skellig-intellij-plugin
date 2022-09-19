package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepExpression : PsiElement {

}

class SkelligTestStepExpressionImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepExpression {

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

    override fun toString(): String {
        return "TestStepExpression: $elementText"
    }
}