package org.skellig.plugin.language.feature.psi

import com.intellij.lang.Language

class SkelligLanguage protected constructor() : Language("Skellig") {
    override fun getDisplayName(): String = "Skellig"

    companion object {
        @Suppress("ktlint:standard:property-naming")
        var INSTANCE = SkelligLanguage()
    }
}
