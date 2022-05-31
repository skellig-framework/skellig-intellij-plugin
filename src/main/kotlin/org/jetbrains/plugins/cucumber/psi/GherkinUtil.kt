package org.jetbrains.plugins.cucumber.psi

import org.jetbrains.plugins.cucumber.psi.impl.GherkinFileImpl

object GherkinUtil {
    fun getFeatureLanguage(gherkinFile: GherkinFile?): String {
        return if (gherkinFile != null) gherkinFile.getLocaleLanguage() else GherkinFileImpl.Companion.defaultLocale
    }
}