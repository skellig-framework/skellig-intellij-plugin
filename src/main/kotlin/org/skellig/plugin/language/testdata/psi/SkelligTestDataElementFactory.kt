package org.skellig.plugin.language.testdata.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import org.skellig.plugin.language.testdata.SkelligTestDataFileType

class SkelligTestDataElementFactory {

    /*companion object {
        fun createProperty(project: Project?, name: String?): SimpleProperty? {
            val file = createFile(project, name)
            return file.firstChild as SimpleProperty
        }

        fun createFile(project: Project?, text: String?): SkelligTestDataFile {
            val name = "dummy.simple"
            return PsiFileFactory.getInstance(project).createFileFromText(name, SkelligTestDataFileType.INSTANCE, text!!) as SkelligTestDataFile
        }

        fun createProperty(project: Project?, name: String, value: String): SimpleProperty? {
            val file = createFile(project, "$name = $value")
            return file.firstChild as SimpleProperty
        }

        fun createCRLF(project: Project?): PsiElement? {
            val file = createFile(project, "\n")
            return file.firstChild
        }
    }*/
}