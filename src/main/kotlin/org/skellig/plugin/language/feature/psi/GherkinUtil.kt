package org.skellig.plugin.language.feature.psi

import org.skellig.plugin.language.feature.psi.impl.GherkinFileImpl

object GherkinUtil {
    fun getFeatureLanguage(gherkinFile: GherkinFile?): String {
        return if (gherkinFile != null) gherkinFile.getLocaleLanguage() else GherkinFileImpl.Companion.defaultLocale
    }
}