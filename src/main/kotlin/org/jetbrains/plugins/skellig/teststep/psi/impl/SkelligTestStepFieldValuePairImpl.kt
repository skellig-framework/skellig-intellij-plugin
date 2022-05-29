// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementTypes
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepFieldValuePair : PsiElement {

    val field: SkelligTestStepText?
    val value: PsiElement?
}

open class SkelligTestStepFieldValuePairImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepFieldValuePair {

    override val field: SkelligTestStepText?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.TEXT)?.psi as SkelligTestStepText?
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