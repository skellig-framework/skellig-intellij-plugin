package org.skellig.plugin.language.teststep.psi

import com.intellij.psi.PsiFile
import org.skellig.plugin.language.teststep.psi.impl.SkelligTestStep

interface SkelligTestStepFile : PsiFile {
    fun getTestSteps(): Array<SkelligTestStep?>
}