package org.jetbrains.plugins.skellig.teststep.psi.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition
import org.jetbrains.plugins.cucumber.steps.reference.AbstractExtension
import org.jetbrains.plugins.cucumber.steps.reference.BDDFrameworkType
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepFileType
import org.jetbrains.plugins.skellig.teststep.psi.impl.SkelligTestStep

class SkelligTestStepExtension : AbstractExtension() {

    override val stepFileType: BDDFrameworkType
        get() = BDDFrameworkType(SkelligTestStepFileType.INSTANCE, "Skellig Test Step")

    override fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        val result = mutableListOf<AbstractStepDefinition>()
        val fileBasedIndex: FileBasedIndex = FileBasedIndex.getInstance()
        val scope: GlobalSearchScope = featureFile?.resolveScope ?: module.getModuleWithDependenciesAndLibrariesScope(true)
        val project = module.project
        fileBasedIndex.processValues(SkelligTestStepStepIndex.INDEX_ID, true, null,
            { file: VirtualFile, _: List<Int> ->
                ProgressManager.checkCanceled()
                val psiFile: PsiFile = PsiManager.getInstance(project).findFile(file) ?: return@processValues true
                for (testStep in psiFile.children) {
                    if (testStep is SkelligTestStep) {
                        result.add(SkelligTestStepDefinition(testStep))
                    }
                }
                true
            }, scope
        )
        return result
    }
}