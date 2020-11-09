package org.skellig.plugin.language.feature.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.skellig.plugin.language.feature.SkelligFeatureFileType
import org.skellig.plugin.language.feature.SkelligFeatureLanguage

class SkelligFeatureFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SkelligFeatureLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return SkelligFeatureFileType.INSTANCE
    }

    override fun toString(): String {
        return "Skellig Feature"
    }
}