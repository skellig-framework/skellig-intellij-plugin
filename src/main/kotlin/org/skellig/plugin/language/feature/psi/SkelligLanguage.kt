package org.skellig.plugin.language.feature.psi

import com.intellij.lang.Language

class SkelligLanguage protected constructor() : Language("Skellig") {
    override fun getDisplayName(): String {
        return "Skellig"
    }

    companion object {
        var INSTANCE = SkelligLanguage()
    }
}