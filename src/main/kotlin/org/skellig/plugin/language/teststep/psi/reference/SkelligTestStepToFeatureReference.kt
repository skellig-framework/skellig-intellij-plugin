package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.feature.psi.*
import java.util.regex.Pattern


open class SkelligTestStepToFeatureReference(myElement: PsiElement, private val range: TextRange) : SkelligTestStepSimpleReference(myElement), PsiPolyVariantReference {

    companion object {
        private val RESOLVER = MyResolver()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return ResolveCache.getInstance(element.project).resolveWithCaching<SkelligTestStepToFeatureReference>(this, RESOLVER, false, incompleteCode)
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun isSoft(): Boolean = false

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolvedResults = multiResolve(false)
        for (rr in resolvedResults) {
            if (element.manager.areElementsEquivalent(rr.element, element)) {
                return true
            }
        }
        return false
    }

    override fun getCanonicalText(): String = element.text

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return element
    }

    override fun getRangeInElement(): TextRange = range

    fun multiResolveInner(): Array<ResolveResult> {
        val resolvedElements: List<SkelligFeatureStepDefinition> =
            CachedValuesManager.getCachedValue(element.containingFile) {
                val allSkelligFeatureFiles = mutableListOf<SkelligFile>()
                val module = ModuleUtilCore.findModuleForPsiElement(element)
                module?.let {
                    val fileBasedIndex = FileBasedIndex.getInstance()
                    val project = module.project

                    val searchScope = module.getModuleWithDependenciesAndLibrariesScope(true)
                        .uniteWith(ProjectScope.getLibrariesScope(project))
                    val files = GlobalSearchScope.getScopeRestrictedByFileTypes(searchScope, SkelligFileType.INSTANCE)

                    val psiManager = PsiManager.getInstance(project)
                    fileBasedIndex.iterateIndexableFiles({ virtualFile: VirtualFile ->
                        if (!virtualFile.isDirectory && files.contains(virtualFile)) {
                            val psiFile = psiManager.findFile(virtualFile)
                            if (psiFile != null && psiFile is SkelligFile) {
                                allSkelligFeatureFiles.add(psiFile)
                            }
                        }
                        true // continue processing
                    }, project, null)

                    val resolvedElements: MutableList<SkelligFeatureStepDefinition> = mutableListOf()
                    val testStepPattern = Pattern.compile(SkelligUtil.getTestStepName(element))
                    for (skelligFeatureFile in allSkelligFeatureFiles) {
                        PsiTreeUtil.getChildrenOfType(skelligFeatureFile, SkelligFeature::class.java)?.forEach { feature ->
                            PsiTreeUtil.getChildrenOfType(feature, SkelligScenarioOutline::class.java)?.forEach { scenario ->
                                PsiTreeUtil.getChildrenOfType(scenario, SkelligFeatureStep::class.java)?.forEach { step ->
                                    if (testStepPattern.matcher(step.substitutedName ?: "").matches()) {
                                        resolvedElements.add(SkelligFeatureStepDefinition(step))
                                    }
                                }
                            }
                        }
                    }
                    CachedValueProvider.Result.create<List<SkelligFeatureStepDefinition>>(resolvedElements, PsiModificationTracker.MODIFICATION_COUNT)
                }
            }

        return resolvedElements.mapNotNull { it.getElement() }.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is SkelligTestStepToFeatureReference) {
            element == other.element
        } else false
    }

    override fun hashCode(): Int {
        return element.hashCode()
    }

    private class MyResolver : ResolveCache.PolyVariantResolver<SkelligTestStepToFeatureReference?> {
        override fun resolve(ref: SkelligTestStepToFeatureReference, incompleteCode: Boolean): Array<ResolveResult> {
            return ref.multiResolveInner()
        }
    }

    class SkelligFeatureStepDefinition(element: PsiElement) {
        private val myElementPointer: SmartPsiElementPointer<PsiElement> = SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)

        fun getElement(): PsiElement? {
            return myElementPointer.element
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as SkelligFeatureStepDefinition
            return myElementPointer == that.myElementPointer
        }

        override fun hashCode(): Int {
            return myElementPointer.hashCode()
        }
    }
}