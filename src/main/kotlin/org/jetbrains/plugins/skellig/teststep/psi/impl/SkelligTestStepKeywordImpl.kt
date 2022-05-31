package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepKeyword : PsiElement

class SkelligTestStepKeywordImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepKeyword {

    override fun getPresentableText(): String {
        return "SkelligTestStepKeyword: $elementText"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}