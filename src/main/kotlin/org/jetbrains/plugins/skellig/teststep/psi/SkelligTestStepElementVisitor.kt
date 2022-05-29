package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.plugins.cucumber.psi.impl.*
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepSimpleValue
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepSimpleValueImpl

abstract class SkelligTestStepElementVisitor : PsiElementVisitor() {

    fun visit(psiElement: PsiElement) {
        visitElement(psiElement)
    }

}