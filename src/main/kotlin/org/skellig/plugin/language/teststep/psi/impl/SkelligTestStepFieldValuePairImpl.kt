package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementTypes
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepFieldValuePair : PsiElement {

    val property: PsiElement?
    val value: PsiElement?
}

open class SkelligTestStepFieldValuePairImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepFieldValuePair {

    override val property: PsiElement?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.PROPERTY)?.psi
        }

    override val value: PsiElement?
        get() {
            var value = node.findChildByType(SkelligTestStepElementTypes.OBJECT)?.psi
            if (value != null) {
                return value
            }

            value = node.findChildByType(SkelligTestStepElementTypes.ARRAY)?.psi
            if (value != null) {
                return value
            }

            return node.findChildByType(SkelligTestStepElementTypes.VALUE)?.psi
        }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}