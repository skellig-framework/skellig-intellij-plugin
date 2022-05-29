package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.lang.Language

class SkelligTestStepLanguage : Language("SkelligTestStep") {

    companion object {
        val INSTANCE = SkelligTestStepLanguage()
    }
}