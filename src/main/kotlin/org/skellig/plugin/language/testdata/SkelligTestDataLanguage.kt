package org.skellig.plugin.language.testdata

import com.intellij.lang.Language

class SkelligTestDataLanguage : Language("SkelligTestData") {

    companion object {
        val INSTANCE = SkelligTestDataLanguage()
    }
}