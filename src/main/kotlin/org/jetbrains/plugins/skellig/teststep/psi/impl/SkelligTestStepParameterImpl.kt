// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepParameter : PsiElement {

}

class SkelligTestStepParameterImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepParameter {

    override fun getReference(): PsiReference? {
//        return GherkinStepParameterReference(this)
        return null
    }

    override fun getName(): String? {
        return text
    }

    override fun getUseScope(): SearchScope {
        return LocalSearchScope(containingFile)
    }

    override fun toString(): String {
        return "TestStepParameter:$text"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}