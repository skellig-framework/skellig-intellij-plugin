package org.skellig.plugin.language.testdata.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.SkelligFeatureFileType
import java.util.*


class SkelligTestDataUtil {

    companion object {
        fun getNameIdentifier(element: SkelligTestDataTestDefinition): PsiElement? {
            return element.node.findChildByType(SkelligTestDataTypes.TEST_DEFINITION)?.psi
        }

        fun getName(element: SkelligTestDataTestDefinition): String {
            val keyNode = element.node.findChildByType(SkelligTestDataTypes.TEST_DEFINITION)
            return keyNode?.text?.replace("\\\\ ".toRegex(), " ") ?: ""
        }

        fun findSteps(project: Project, key: String): Collection<SkelligTestDataTestDefinition> {
            val result: MutableList<SkelligTestDataTestDefinition> = ArrayList<SkelligTestDataTestDefinition>()
            val virtualFiles = FileTypeIndex.getFiles(SkelligFeatureFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile: SkelligTestDataFile? = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligTestDataFile?
                if (simpleFile != null) {
                    val properties: Array<SkelligTestDataTestDefinition?>? = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligTestDataTestDefinition::class.java)
                    properties!!.filter { property -> key == property!!.name }.forEach { property -> result.add(property!!) }
                }
            }
            return result
        }

        fun findSteps(project: Project): Collection<SkelligTestDataTestDefinition> {
            val result: MutableCollection<SkelligTestDataTestDefinition> = ArrayList<SkelligTestDataTestDefinition>()
            val virtualFiles = FileTypeIndex.getFiles(SkelligFeatureFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile: SkelligTestDataFile? = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligTestDataFile?
                if (simpleFile != null) {
                    val properties: Array<SkelligTestDataTestDefinition?>? = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligTestDataTestDefinition::class.java)
                    properties!!.forEach { property -> result.add(property!!) }
                }
            }
            return result
        }
    }
}