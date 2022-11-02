package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligFileType private constructor() : LanguageFileType(SkelligLanguage.INSTANCE) {
    override fun getName(): String {
        return "Skellig"
    }

    override fun getDescription(): String {
        return "Skellig File"
    }

    override fun getDefaultExtension(): String {
        return "skellig;sf;sfeature"
    }

    override fun getIcon(): Icon {
        return  SkelligFileIcons.FEATURE_FILE
    }

    companion object {
        val INSTANCE = SkelligFileType()
    }
}