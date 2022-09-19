package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepFunction : PsiElement {

}

class SkelligTestStepFunctionImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepFunction {

    override fun getPresentableText(): String {
        return "Test Step: $elementText"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}