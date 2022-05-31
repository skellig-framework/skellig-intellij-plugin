package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.PsiFile
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStep

interface SkelligTestStepFile : PsiFile {
    fun getTestSteps(): Array<SkelligTestStep?>
}