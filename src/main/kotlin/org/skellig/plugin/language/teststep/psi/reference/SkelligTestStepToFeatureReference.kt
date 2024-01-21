package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.feature.psi.*
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import java.util.regex.Pattern


open class SkelligTestStepToFeatureReference(stepParameter: SkelligTestStepTestStepName) : SkelligTestStepSimpleReference(stepParameter), PsiPolyVariantReference {

    val element: SkelligTestStepTestStepName
        get() = super.getElement() as SkelligTestStepTestStepName

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        return module?.let {
            val foundTestSteps = findAllUsagesInSkelligFeatureFiles(it, Pattern.compile(element.name))
            val results = mutableListOf<ResolveResult>()
            for (testStep in foundTestSteps) {
                results.add(PsiElementResolveResult(testStep))
            }
            results.toTypedArray()
        } ?: emptyArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val variants = mutableListOf<LookupElement>()
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        module?.let {
            val stateValues = findAllUsagesInSkelligFeatureFiles(module, Pattern.compile(element.name))
            for (item in stateValues) {
                variants.add(
                    LookupElementBuilder
                        .create(item.text).withIcon(SkelligFileIcons.TEST_DATA_FILE)
                        .withTypeText(item.containingFile.name)
                )
            }
        }
        return variants.toTypedArray()
    }

    private fun findAllUsagesInSkelligFeatureFiles(module: com.intellij.openapi.module.Module, testStepPattern: Pattern): List<SkelligFeatureStep> {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val project = module.project

        val searchScope = module.getModuleWithDependenciesAndLibrariesScope(true)
            .uniteWith(ProjectScope.getLibrariesScope(project))
        val files = GlobalSearchScope.getScopeRestrictedByFileTypes(searchScope, SkelligFileType.INSTANCE)

        val elements = mutableListOf<SkelligFeatureStep>()
        val psiManager = PsiManager.getInstance(project)

        // Iterate through VirtualFiles in the scope
        fileBasedIndex.iterateIndexableFiles({ virtualFile: VirtualFile ->
            if (!virtualFile.isDirectory && files.contains(virtualFile)) {
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile != null && psiFile is SkelligFile) {
                    PsiTreeUtil.getChildrenOfType(psiFile, SkelligFeature::class.java)?.forEach { feature ->
                        PsiTreeUtil.getChildrenOfType(feature, SkelligScenarioOutline::class.java)?.forEach { scenario ->
                            PsiTreeUtil.getChildrenOfType(scenario, SkelligFeatureStep::class.java)?.forEach { step ->
                                if (testStepPattern.matcher(step.substitutedName ?: "").matches()) {
                                    elements.add(step)
                                }
                            }
                        }
                    }

                }
            }
            true // continue processing
        }, project, null)

        return elements
    }


}