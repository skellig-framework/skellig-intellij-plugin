package org.skellig.plugin.language.feature.psi

import org.skellig.plugin.language.feature.psi.impl.SkelligFileImpl

object SkelligUtil {
    fun getFeatureLanguage(skelligFile: SkelligFile?): String {
        return skelligFile?.getLocaleLanguage() ?: SkelligFileImpl.defaultLocale
    }
}