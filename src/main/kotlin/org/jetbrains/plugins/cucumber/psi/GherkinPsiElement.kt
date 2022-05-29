// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepExpression
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepParameter
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStepText

interface GherkinPsiElement : PsiElement