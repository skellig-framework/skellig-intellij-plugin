package org.skellig.plugin.language.feature.steps.reference

import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import java.io.File
import java.util.*

abstract class AbstractStepDefinitionCreator : StepDefinitionCreator {

    companion object {
        private fun findStepDefinitionDirectory(featureFile: PsiFile): PsiDirectory? {
            val psiFeatureDir = featureFile.containingDirectory!!
            var featureDir: VirtualFile? = psiFeatureDir.virtualFile
            val contentRoot = ProjectRootManager.getInstance(featureFile.project).fileIndex.getContentRootForFile(featureDir!!)
            while (featureDir != null &&
                !Comparing.equal(featureDir, contentRoot) && featureDir.findChild(org.skellig.plugin.language.feature.SkelligUtil.STEP_DEFINITIONS_DIR_NAME) == null
            ) {
                featureDir = featureDir.parent
            }
            if (featureDir != null) {
                val stepsDir = featureDir.findChild(org.skellig.plugin.language.feature.SkelligUtil.STEP_DEFINITIONS_DIR_NAME)
                if (stepsDir != null) {
                    return featureFile.manager.findDirectory(stepsDir)
                }
            }
            return null
        }
    }

    override fun getStepDefinitionFilePath(psiFile: PsiFile): String {
        val file = psiFile.virtualFile!!
        var parent = file.parent
        // if file is direct child of step definitions dir
        if (parent != null && org.skellig.plugin.language.feature.SkelligUtil.STEP_DEFINITIONS_DIR_NAME == parent.name) {
            return file.name
        }

        // in subfolder
        val dirsReversed: MutableList<String> = ArrayList()
        while (parent != null) {
            val name = parent.name
            if (org.skellig.plugin.language.feature.SkelligUtil.STEP_DEFINITIONS_DIR_NAME == name) {
                break
            }
            dirsReversed.add(name)
            parent = parent.parent
        }
        val buf = StringBuilder()
        for (i in dirsReversed.indices.reversed()) {
            buf.append(dirsReversed[i]).append(File.separatorChar)
        }
        buf.append(file.name)
        return buf.toString()
    }

    override fun getDefaultStepDefinitionFolderPath(step: SkelligFeatureStep): String {
        val featureFile = step.containingFile
        val dir = findStepDefinitionDirectory(featureFile)
        return dir?.virtualFile?.path ?: FileUtil.join(
            featureFile.containingDirectory.virtualFile.path,
            org.skellig.plugin.language.feature.SkelligUtil.STEP_DEFINITIONS_DIR_NAME
        )
    }

    protected fun closeActiveTemplateBuilders(file: PsiFile) {
        val project = file.project
        val vFile = Objects.requireNonNull(file.virtualFile)
        val descriptor = OpenFileDescriptor(project, vFile)
        FileEditorManager.getInstance(project).getAllEditors(vFile)
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
        val editor = FileEditorManager.getInstance(project).selectedTextEditor!!
        val templateManager = TemplateManager.getInstance(file.project)
        val templateState = TemplateManagerImpl.getTemplateState(editor)
        val template = templateManager.getActiveTemplate(editor)
        if (templateState != null && template != null) {
            templateState.gotoEnd(false)
        }
    }
}