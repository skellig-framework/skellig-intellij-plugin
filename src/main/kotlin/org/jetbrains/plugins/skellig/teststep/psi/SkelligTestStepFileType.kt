package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class SkelligTestStepFileType private constructor() : LanguageFileType(SkelligTestStepLanguage.INSTANCE) {
    override fun getName(): String {
        return "SkelligTestStep"
    }

    override fun getDescription(): String {
        return "Skellig Test Step File"
    }

    override fun getDefaultExtension(): String {
        return "sts"
    }

    override fun getIcon(): Icon {
        return IconLoader.getIcon("/icons/jar-gray.png")
    }

    companion object {
        val INSTANCE = SkelligTestStepFileType()
    }
}