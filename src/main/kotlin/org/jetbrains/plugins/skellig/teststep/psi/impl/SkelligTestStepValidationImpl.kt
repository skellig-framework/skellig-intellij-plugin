// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

interface SkelligTestStepValidation {
    val value: PsiElement?
}

class SkelligTestStepValidationImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node), SkelligTestStepValidation {

    override fun toString(): String {
        return "SkelligTestStepValidation: $elementText"
    }
}