package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepArray : PsiElement {
    val values: List<PsiElement>
}

class SkelligTestStepArrayImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepArray {

    override val values: List<PsiElement>
        get() {
            val simpleValues = PsiTreeUtil.getChildrenOfTypeAsList(this, SkelligTestStepSimpleValue::class.java)
            val objects = PsiTreeUtil.getChildrenOfTypeAsList(this, SkelligTestStepObject::class.java)
            val array = PsiTreeUtil.getChildrenOfTypeAsList(this, SkelligTestStepArray::class.java)
            return simpleValues.union(objects).union(array).toList()
        }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}