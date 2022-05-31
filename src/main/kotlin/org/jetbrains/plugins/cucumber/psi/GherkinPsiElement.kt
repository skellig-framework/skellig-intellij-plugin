package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepExpression
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepParameter
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepText

interface GherkinPsiElement : PsiElement