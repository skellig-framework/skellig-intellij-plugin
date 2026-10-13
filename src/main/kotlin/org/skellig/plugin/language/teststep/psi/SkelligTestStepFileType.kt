package org.skellig.plugin.language.teststep.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligTestStepFileType private constructor() : LanguageFileType(SkelligTestStepLanguage.INSTANCE) {
    override fun getName(): String = "SkelligTestStep"

    override fun getDescription(): String = "Skellig Test Step File"

    override fun getDefaultExtension(): String = "sts;ts"

    override fun getIcon(): Icon = SkelligFileIcons.TEST_DATA_FILE

    companion object {
        val INSTANCE = SkelligTestStepFileType()
    }
}
