// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

interface SkelligTestStepRequest : PsiElement {
    val value: PsiElement?
}

class SkelligTestStepRequestImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepRequest{

    override fun toString(): String {
        return "SkelligTestStepRequest: $elementText"
    }
}