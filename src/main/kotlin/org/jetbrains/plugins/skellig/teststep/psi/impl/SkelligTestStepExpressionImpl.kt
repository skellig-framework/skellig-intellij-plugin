// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

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