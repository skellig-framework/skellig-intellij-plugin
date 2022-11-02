package org.skellig.plugin.language.teststep.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligTestStepFileType private constructor() : LanguageFileType(SkelligTestStepLanguage.INSTANCE) {
    override fun getName(): String {
        return "SkelligTestStep"
    }

    override fun getDescription(): String {
        return "Skellig Test Step File"
    }

    override fun getDefaultExtension(): String {
        return "sts;ts"
    }

    override fun getIcon(): Icon {
        return SkelligFileIcons.TEST_DATA_FILE
    }

    companion object {
        val INSTANCE = SkelligTestStepFileType()
    }
}