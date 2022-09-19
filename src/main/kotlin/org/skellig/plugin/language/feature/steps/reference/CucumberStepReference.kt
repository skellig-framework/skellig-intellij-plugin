package org.skellig.plugin.language.feature.steps.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.containers.map2Array
import org.skellig.plugin.language.feature.psi.impl.GherkinStepImpl
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

class CucumberStepReference(step: PsiElement, range: TextRange) : PsiPolyVariantReference {

    companion object {
        private val RESOLVER = MyResolver()
    }

    private val myStep: PsiElement
    private val myRange: TextRange

    init {
        myStep = step
        myRange = range
    }

    override fun getElement(): PsiElement = myStep

    override fun getRangeInElement(): TextRange = myRange


    override fun resolve(): PsiElement? {
        val result = multiResolve(true)
        return if (result.size == 1) result[0].element else null
    }

    override fun getCanonicalText(): String = myStep.text

    override fun handleElementRename(newElementName: String): PsiElement {
        return myStep
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return myStep
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
        return ResolveCache.getInstance(element.project).resolveWithCaching<CucumberStepReference>(this, RESOLVER, false, incompleteCode)
    }

    private fun multiResolveInner(): Array<ResolveResult> {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(myStep) ?: return ResolveResult.EMPTY_ARRAY
        val frameworks = SkelligExtensionPoint.EP_NAME.extensionList
        val stepVariants = frameworks.mapNotNull { e -> e.getStepName(myStep) }.toSet()
        if (stepVariants.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY
        }
        val featureFile: PsiFile = myStep.containingFile
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
            if (stepDefinition.supportsStep(myStep)) {
                for (stepVariant in stepVariants) {
                    val element: PsiElement? = stepDefinition.getElement()
                    if (stepDefinition.matches(stepVariant) && element != null && !resolvedElements.contains(element)) {
                        resolvedElements.add(element)
                        break
                    }
                }
            }
        }
        return resolvedElements.map2Array { PsiElementResolveResult(it) }
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
        return CucumberStepHelper.findStepDefinitions(myStep.containingFile, myStep as GherkinStepImpl)
    }

    private class MyResolver : ResolveCache.PolyVariantResolver<CucumberStepReference?> {
        override fun resolve(ref: CucumberStepReference, incompleteCode: Boolean): Array<ResolveResult> {
            return ref.multiResolveInner()
        }
    }

}