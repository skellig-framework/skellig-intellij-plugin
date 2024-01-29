package org.skellig.plugin.language.feature.steps.reference.java

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierList
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.feature.steps.reference.AbstractExtension
import org.skellig.plugin.language.feature.steps.reference.BDDFrameworkType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFileType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepDefinition

class SkelligJavaTestStepExtension : AbstractExtension() {

    override val stepFileType: BDDFrameworkType
        get() = BDDFrameworkType(JavaFileType.INSTANCE, "Skellig Java Test Step")

    override fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        val result = mutableListOf<AbstractStepDefinition>()
        val fileBasedIndex: FileBasedIndex = FileBasedIndex.getInstance()
        val scope: GlobalSearchScope = featureFile?.resolveScope ?: module.getModuleWithDependenciesAndLibrariesScope(true)
        val project = module.project
        fileBasedIndex.processValues(
            SkelligJavaTestStepStepIndex.INDEX_ID, true, null,
            { file: VirtualFile, _: List<Int> ->
                ProgressManager.checkCanceled()
                val psiFile: PsiFile = PsiManager.getInstance(project).findFile(file) ?: return@processValues true
                val psiClass = PsiTreeUtil.getChildrenOfType(psiFile, PsiClassImpl::class.java)!![0]
                PsiTreeUtil.getChildrenOfType(psiClass, PsiMethod::class.java)?.forEach { method ->
                    PsiTreeUtil.getChildrenOfType(method, PsiModifierList::class.java)?.let { modifiers ->
                        PsiTreeUtil.getChildrenOfType(modifiers[0], PsiAnnotation::class.java)?.forEach {
                            if(it.nameReferenceElement?.text == "TestStep") {
                                result.add(SkelligJavaTestStepDefinition(it))
                            }
                        }
                    }
                }
                true
            }, scope
        )
        return result
    }
}