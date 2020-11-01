package org.skellig.plugin.language.testdata.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.skellig.plugin.language.testdata.SkelligTestDataFileType
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage

class SkelligTestDataFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SkelligTestDataLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return SkelligTestDataFileType.INSTANCE
    }

    override fun toString(): String {
        return "Skellig Test Data"
    }
}