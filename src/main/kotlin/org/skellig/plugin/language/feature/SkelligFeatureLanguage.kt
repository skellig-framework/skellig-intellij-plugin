package org.skellig.plugin.language.feature

import com.intellij.lang.Language

class SkelligFeatureLanguage : Language("SkelligFeature") {

    companion object {
        val INSTANCE = SkelligFeatureLanguage()
    }
}