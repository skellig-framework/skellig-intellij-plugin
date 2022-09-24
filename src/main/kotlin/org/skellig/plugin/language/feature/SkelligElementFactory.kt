package org.skellig.plugin.language.feature

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import org.skellig.plugin.language.feature.psi.SkelligFileType

object SkelligElementFactory {
    fun createTempPsiFile(project: Project?, text: String): PsiElement {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "temp." + SkelligFileType.INSTANCE.defaultExtension,
            SkelligFileType.INSTANCE,
            text, LocalTimeCounter.currentTime(), true
        )
    }
}