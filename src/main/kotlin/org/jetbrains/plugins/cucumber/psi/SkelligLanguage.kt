package org.jetbrains.plugins.cucumber.psi

import com.intellij.lang.Language

class SkelligLanguage protected constructor() : Language("Skellig") {
    override fun getDisplayName(): String {
        return "Skellig"
    }

    companion object {
        var INSTANCE = SkelligLanguage()
    }
}