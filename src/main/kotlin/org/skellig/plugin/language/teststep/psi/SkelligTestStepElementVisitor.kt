package org.skellig.plugin.language.teststep.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

abstract class SkelligTestStepElementVisitor : PsiElementVisitor() {

    fun visit(psiElement: PsiElement) {
        visitElement(psiElement)
    }

}