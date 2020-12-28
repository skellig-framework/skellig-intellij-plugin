package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.SkelligFeatureFileType
import java.util.*


class SkelligFeatureStepsUtil {

    companion object {
        fun getNameIdentifier(element: SkelligFeatureStep): PsiElement? {
            return element.node.findChildByType(SkelligFeatureTypes.FEATURE)?.psi
        }

        fun getName(element: SkelligFeatureStep): String? {
            val keyNode = element.node.findChildByType(SkelligFeatureTypes.STEP)
            return keyNode?.text ?: ""
        }

        fun findSteps(project: Project, key: String): Collection<SkelligFeatureStep> {
            val result: MutableList<SkelligFeatureStep> = ArrayList<SkelligFeatureStep>()
            val virtualFiles = FileTypeIndex.getFiles(SkelligFeatureFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile: SkelligFeatureFile? = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligFeatureFile?
                if (simpleFile != null) {
                    val properties: Array<SkelligFeatureStep?>? = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligFeatureStep::class.java)
                    properties!!.filter { property -> key == property!!.name }.forEach { property -> result.add(property!!) }
                }
            }
            return result
        }

        fun findSteps(project: Project): Collection<SkelligFeatureStep> {
            val result: MutableCollection<SkelligFeatureStep> = ArrayList<SkelligFeatureStep>()
            val virtualFiles = FileTypeIndex.getFiles(SkelligFeatureFileType.INSTANCE, GlobalSearchScope.allScope(project))
            for (virtualFile in virtualFiles) {
                val simpleFile: SkelligFeatureFile? = PsiManager.getInstance(project).findFile(virtualFile!!) as SkelligFeatureFile?
                if (simpleFile != null) {
                    val properties: Array<SkelligFeatureStep?>? = PsiTreeUtil.getChildrenOfType(simpleFile, SkelligFeatureStep::class.java)
                    properties!!.forEach { property -> result.add(property!!) }
                }
            }
            return result
        }
    }
}