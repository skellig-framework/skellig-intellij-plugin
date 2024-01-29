package org.skellig.plugin.language.feature.steps.reference.kotlin

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.feature.steps.reference.AbstractExtension
import org.skellig.plugin.language.feature.steps.reference.BDDFrameworkType

class SkelligKotlinTestStepExtension : AbstractExtension() {

    override val stepFileType: BDDFrameworkType
        get() = BDDFrameworkType(KotlinFileType.INSTANCE, "Skellig Kotlin Test Step")

    override fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        val result = mutableListOf<AbstractStepDefinition>()
        val fileBasedIndex: FileBasedIndex = FileBasedIndex.getInstance()
        val scope: GlobalSearchScope = featureFile?.resolveScope ?: module.getModuleWithDependenciesAndLibrariesScope(true)
        val project = module.project
        fileBasedIndex.processValues(
            SkelligKotlinTestStepStepIndex.INDEX_ID, true, null,
            { file: VirtualFile, _: List<Int> ->
                ProgressManager.checkCanceled()
                val psiFile: PsiFile = PsiManager.getInstance(project).findFile(file) ?: return@processValues true
                PsiTreeUtil.getChildrenOfType(psiFile, KtClass::class.java)?.forEach { ktClass ->
                    ktClass.getChildOfType<KtClassBody>()?.let { classBody ->
                        PsiTreeUtil.getChildrenOfType(classBody, KtNamedFunction::class.java)?.forEach { method ->
                            PsiTreeUtil.getChildrenOfType(method, KtDeclarationModifierList::class.java)?.forEach { modifiers ->
                                PsiTreeUtil.getChildrenOfType(modifiers, KtAnnotationEntry::class.java)?.forEach { annotation ->
                                    if (annotation.getChildOfType<KtConstructorCalleeExpression>()?.text == "TestStep") {
                                        val testStepDef = SkelligUtil.getTestStepName(annotation)
                                        if (testStepDef.isNotEmpty()) {
                                            result.add(SkelligKotlinTestStepDefinition(annotation))
                                        }
                                    }
                                }
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