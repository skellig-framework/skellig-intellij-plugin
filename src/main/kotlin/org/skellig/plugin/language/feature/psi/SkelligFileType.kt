package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class SkelligFileType private constructor() : LanguageFileType(SkelligLanguage.INSTANCE) {
    override fun getName(): String {
        return "Skellig"
    }

    override fun getDescription(): String {
        return "Skellig File"
    }

    override fun getDefaultExtension(): String {
        return "skellig"
    }

    override fun getIcon(): Icon {
        return IconLoader.getIcon("/icons/jar-gray.png")
    }

    companion object {
        val INSTANCE = SkelligFileType()
    }
}