package org.skellig.plugin.language.teststep.psi

import com.intellij.lang.Language

class SkelligTestStepLanguage : Language("SkelligTestStep") {

    companion object {
        val INSTANCE = SkelligTestStepLanguage()
    }
}