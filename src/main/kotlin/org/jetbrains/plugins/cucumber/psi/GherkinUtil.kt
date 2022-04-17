// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import org.jetbrains.plugins.cucumber.psi.impl.GherkinFileImpl

object GherkinUtil {
    fun getFeatureLanguage(gherkinFile: GherkinFile?): String {
        return if (gherkinFile != null) gherkinFile.getLocaleLanguage() else GherkinFileImpl.Companion.defaultLocale
    }
}