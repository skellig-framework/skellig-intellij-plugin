package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligFileType private constructor() : LanguageFileType(SkelligLanguage.INSTANCE) {
    override fun getName(): String = "Skellig"

    override fun getDescription(): String = "Skellig File"

    override fun getDefaultExtension(): String = "skellig;sf;sfeature"

    override fun getIcon(): Icon = SkelligFileIcons.FEATURE_FILE

    companion object {
        val INSTANCE = SkelligFileType()
    }
}
