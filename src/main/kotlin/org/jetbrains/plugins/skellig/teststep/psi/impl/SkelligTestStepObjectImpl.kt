// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

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