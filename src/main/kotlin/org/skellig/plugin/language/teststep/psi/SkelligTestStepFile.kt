package org.skellig.plugin.language.teststep.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class SkelligTestStepFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, SkelligTestStepLanguage.INSTANCE) {
    override fun getFileType(): FileType = SkelligTestStepFileType.INSTANCE

    override fun toString(): String = "Skellig Test Step File"
}
