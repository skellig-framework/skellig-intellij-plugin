package org.skellig.plugin.language.feature

import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligFeatureFileType : LanguageFileType(SkelligFeatureLanguage.INSTANCE) {

    companion object {
        val INSTANCE = SkelligFeatureFileType()
    }

    @NotNull
    override fun getName(): String {
        return "Skellig Feature File"
    }

    @NotNull
    override fun getDescription(): String {
        return "Skellig Feature File"
    }

    @NotNull
    override fun getDefaultExtension(): String {
        return "sf"
    }

    @Nullable
    override fun getIcon(): Icon? {
        return SkelligFileIcons.FEATURE_FILE
    }
}