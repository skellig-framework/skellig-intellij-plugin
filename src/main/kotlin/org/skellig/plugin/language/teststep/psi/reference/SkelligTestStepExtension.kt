package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.feature.steps.reference.AbstractExtension
import org.skellig.plugin.language.feature.steps.reference.BDDFrameworkType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFileType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression

class SkelligTestStepExtension : AbstractExtension() {

    override val stepFileType: BDDFrameworkType
        get() = BDDFrameworkType(SkelligTestStepFileType.INSTANCE, "Skellig Test Step")

    override fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        val result = mutableListOf<AbstractStepDefinition>()
        val fileBasedIndex: FileBasedIndex = FileBasedIndex.getInstance()
        val scope: GlobalSearchScope = featureFile?.resolveScope ?: module.getModuleWithDependenciesAndLibrariesScope(true)
        val project = module.project
        fileBasedIndex.processValues(
            SkelligTestStepStepIndex.INDEX_ID, true, null,
            { file: VirtualFile, _: List<Int> ->
                ProgressManager.checkCanceled()
                val psiFile: PsiFile = PsiManager.getInstance(project).findFile(file) ?: return@processValues true
                for (testStep in psiFile.children) {
                    if (testStep is SkelligTestStepTestStepNameExpression) {
                        result.add(SkelligTestStepDefinition(testStep))
                    }
                }
                true
            }, scope
        )
        return result
    }
}