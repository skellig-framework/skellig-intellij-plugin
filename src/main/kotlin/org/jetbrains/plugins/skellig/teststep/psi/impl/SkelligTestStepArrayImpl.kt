// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

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