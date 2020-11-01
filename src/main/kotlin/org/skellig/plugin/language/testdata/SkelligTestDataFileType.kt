package org.skellig.plugin.language.testdata

import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import javax.swing.Icon

class SkelligTestDataFileType : LanguageFileType(SkelligTestDataLanguage.INSTANCE) {

    companion object {
        val INSTANCE = SkelligTestDataFileType()
    }

    @NotNull
    override fun getName(): String {
        return "Skellig Test Data File"
    }

    @NotNull
    override fun getDescription(): String {
        return "Skellig Test Data File"
    }

    @NotNull
    override fun getDefaultExtension(): String {
        return "std"
    }

    @Nullable
    override fun getIcon(): Icon? {
        return SkelligFileIcons.FILE
    }
}