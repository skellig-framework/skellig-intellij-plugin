package org.jetbrains.plugins.cucumber.steps.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.cucumber.CucumberUtil
import org.jetbrains.plugins.cucumber.psi.GherkinFile
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition
import org.jetbrains.plugins.cucumber.steps.reference.SkelligExtensionPoint

abstract class AbstractExtension : SkelligExtensionPoint {

    override fun isStepLikeFile(child: PsiElement, parent: PsiElement): Boolean {
        return false
    }

    override fun isWritableStepLikeFile(child: PsiElement, parent: PsiElement): Boolean {
        val file: PsiFile = child.containingFile
        val vFile: VirtualFile = file.virtualFile
        val rootForFile: VirtualFile? = ProjectRootManager.getInstance(child.project).fileIndex.getSourceRootForFile(vFile)
        return rootForFile != null
    }

    override fun getStepDefinitionContainers(file: GherkinFile): Collection<PsiFile> {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(file) ?: return emptySet()
        val stepDefs: List<AbstractStepDefinition> = CucumberUtil.loadFrameworkSteps(this, file, module)
        val result = mutableSetOf<PsiFile>()
        for (stepDef in stepDefs) {
            val stepDefElement: PsiElement? = stepDef.getElement()
            val psiFile: PsiFile? = stepDefElement?.containingFile
            val psiDirectory: PsiDirectory? = psiFile?.parent
            if (psiDirectory != null && isWritableStepLikeFile(psiFile, psiDirectory)) {
                result.add(psiFile)
            }
        }
        return result
    }
}