package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepObject : PsiElement {
     val values: List<SkelligTestStepFieldValuePair>
}

open class SkelligTestStepObjectImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepObject {

    override val values: List<SkelligTestStepFieldValuePair>
        get() {
            return PsiTreeUtil.getChildrenOfTypeAsList(this, SkelligTestStepFieldValuePair::class.java)
        }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}