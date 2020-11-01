package org.skellig.plugin.language.testdata

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.psi.SkelligTestDataDefinition
import org.skellig.plugin.language.psi.SkelligTestDataTestDefinition
import org.skellig.plugin.language.testdata.psi.SkelligTestDataFile
import java.util.*


class SkelligTestDataUtil {

    companion object {
        /**
         * Searches the entire project for Simple language files with instances of the Simple property with the given key.
         *
         * @param project current project
         * @param key     to check
         * @return matching properties
         */
        fun findProperties(project: Project, key: String): List<SkelligTestDataTestDefinition?> {
            val result: MutableList<SkelligTestDataTestDefinition?> = ArrayList()
            val virtualFiles = FileTypeIndex.getFiles(SkelligTestDataFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligTestDataFile?
                if (simpleFile != null) {
                    val properties = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligTestDataTestDefinition::class.java)
                    if (properties != null) {
                        for (property in properties) {
                            if (property != null) {
                                if (key == property.text) {
                                    result.add(property)
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

        fun findProperties(project: Project): List<SkelligTestDataTestDefinition?> {
            val result: MutableList<SkelligTestDataTestDefinition?> = mutableListOf()
            val virtualFiles = FileTypeIndex.getFiles(SkelligTestDataFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligTestDataFile?
                if (simpleFile != null) {
                    val properties = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligTestDataTestDefinition::class.java)
                    if (properties != null) {
                        result.addAll(properties)
                    }
                }
            }
            return result
        }
    }
}