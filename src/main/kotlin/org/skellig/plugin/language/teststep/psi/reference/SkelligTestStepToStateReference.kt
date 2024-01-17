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
import org.skellig.plugin.language.teststep.psi.*


open class SkelligTestStepToStateReference(stepParameter: SkelligTestStepFunctionExpression) : SkelligTestStepSimpleReference(stepParameter), PsiPolyVariantReference {

    val element: SkelligTestStepFunctionExpression
        get() = super.getElement() as SkelligTestStepFunctionExpression

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        return module?.let {
            val stateValues = findAllStateValues(it)
            val results = mutableListOf<ResolveResult>()
            for (item in stateValues) {
                if (element.argList.isNotEmpty() && item.text == element.argList[0].text)
                    results.add(PsiElementResolveResult(item))
            }
            results.toTypedArray()
        } ?: emptyArray()
    }

    override fun resolve(): PsiElement? {
        val parameterText = element.functionName.text
        if (parameterText == "get") {
            val resolveResults = multiResolve(false)
            return if (resolveResults.size == 1) resolveResults[0].element else null
        }
        return null
    }

    override fun getVariants(): Array<Any> {
        val variants = mutableListOf<LookupElement>()
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        module?.let {
            val stateValues = findAllStateValues(module)
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

    private fun findAllStateValues(module: com.intellij.openapi.module.Module): List<SkelligTestStepKey> {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val project = module.project

        val searchScope = module.getModuleWithDependenciesAndLibrariesScope(true)
            .uniteWith(ProjectScope.getLibrariesScope(project))
        val files = GlobalSearchScope.getScopeRestrictedByFileTypes(searchScope, SkelligTestStepFileType.INSTANCE)

        val elements = mutableListOf<SkelligTestStepKey>()
        val psiManager = PsiManager.getInstance(project)

        // Iterate through VirtualFiles in the scope
        fileBasedIndex.iterateIndexableFiles({ virtualFile: VirtualFile ->
            if (!virtualFile.isDirectory && files.contains(virtualFile)) {
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile != null && psiFile is SkelligTestStepFile) {
                    psiFile.children.forEach { testStep ->
                        PsiTreeUtil.getChildrenOfType(testStep, SkelligTestStepPair::class.java)?.forEach { pair ->
                            if (pair.key.text == "state") {
                                pair.map?.let { values ->
                                    values.pairList.forEach {
                                        elements.add(it.key)
                                    }
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