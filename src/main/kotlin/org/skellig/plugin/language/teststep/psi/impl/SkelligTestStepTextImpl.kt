package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepText : PsiElement

open class SkelligTestStepTextImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepText {

    override fun getPresentableText(): String {
        return "SkelligTestStepText: $elementText"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}