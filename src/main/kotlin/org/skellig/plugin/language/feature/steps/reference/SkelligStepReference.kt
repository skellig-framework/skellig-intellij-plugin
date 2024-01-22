package org.skellig.plugin.language.feature.steps.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.skellig.plugin.language.feature.psi.impl.SkelligFeatureStepImpl
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

class SkelligStepReference(val step: PsiElement, private val range: TextRange) : PsiPolyVariantReference {

    companion object {
        private val RESOLVER = MyResolver()
    }

    override fun getElement(): PsiElement = step

    override fun getRangeInElement(): TextRange = range


    override fun resolve(): PsiElement? {
        val result = multiResolve(true)
        return if (result.size == 1) result[0].element else null
    }

    override fun getCanonicalText(): String = step.text

    override fun handleElementRename(newElementName: String): PsiElement {
        return step
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return step
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolvedResults = multiResolve(false)
        for (rr in resolvedResults) {
            if (element.getManager().areElementsEquivalent(rr.element, element)) {
                return true
            }
        }
        return false
    }

    override fun isSoft(): Boolean = false

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return ResolveCache.getInstance(element.project).resolveWithCaching<SkelligStepReference>(this, RESOLVER, false, incompleteCode)
    }

    private fun multiResolveInner(): Array<ResolveResult> {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(step) ?: return ResolveResult.EMPTY_ARRAY
        val frameworks = SkelligExtensionPoint.EP_NAME.extensionList
        val stepVariants =
            if (element is PsiLiteralExpression) {
                val text = element.text
                setOf(text.substring(1, text.length - 1))
            } else frameworks.mapNotNull { f -> f.getStepName(step) }.toSet()
        if (stepVariants.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY
        }
        val featureFile: PsiFile = step.containingFile
        val stepDefinitions: List<AbstractStepDefinition> =
            CachedValuesManager.getCachedValue(featureFile) {
                val allStepDefinition = mutableListOf<AbstractStepDefinition>()
                for (framework in frameworks) {
                    allStepDefinition.addAll(framework.loadStepsFor(featureFile, module))
                }
                CachedValueProvider.Result.create<List<AbstractStepDefinition>>(allStepDefinition, PsiModificationTracker.MODIFICATION_COUNT)
            }
        val resolvedElements: MutableList<PsiElement> = mutableListOf()
        for (stepDefinition in stepDefinitions) {
            if (stepDefinition.supportsStep(step)) {
                for (stepVariant in stepVariants) {
                    val element: PsiElement? = stepDefinition.getElement()
                    if (stepDefinition.matches(stepVariant) && element != null && !resolvedElements.contains(element)) {
                        resolvedElements.add(element)
//                        break
                    }
                }
            }
        }
        return resolvedElements.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    /**
     * @return first definition (if any) or null if no definition found
     * @see .resolveToDefinitions
     */
    fun resolveToDefinition(): AbstractStepDefinition? {
        val definitions: Collection<AbstractStepDefinition> = resolveToDefinitions()
        return if (definitions.isEmpty()) null else definitions.iterator().next()
    }

    /**
     * @return step definitions
     * @see .resolveToDefinition
     */
    private fun resolveToDefinitions(): Collection<AbstractStepDefinition> {
        return SkelligStepHelper.findStepDefinitions(step.containingFile, step as SkelligFeatureStepImpl)
    }

    private class MyResolver : ResolveCache.PolyVariantResolver<SkelligStepReference?> {
        override fun resolve(ref: SkelligStepReference, incompleteCode: Boolean): Array<ResolveResult> {
            return ref.multiResolveInner()
        }
    }

}