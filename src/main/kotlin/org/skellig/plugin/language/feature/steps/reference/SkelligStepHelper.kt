package org.skellig.plugin.language.feature.steps.reference

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import java.util.regex.Pattern

object SkelligStepHelper {

    /**
     * Searches for ALL step definitions, groups it by step definition class and sorts by pattern size.
     * For each step definition class it finds the largest pattern.
     *
     * @param featureFile file with steps
     * @param step        step itself
     * @return definitions
     */
    fun findStepDefinitions(featureFile: PsiFile, step: SkelligFeatureStep): Collection<AbstractStepDefinition> {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(featureFile) ?: return emptyList()
        val substitutedName: String = step.substitutedName ?: return emptyList()
        val definitionsByClass = mutableMapOf<Class<out AbstractStepDefinition>, AbstractStepDefinition>()
        val allSteps = loadStepsFor(featureFile, module)
        for (stepDefinition in allSteps) {
            if (stepDefinition.matches(substitutedName) && stepDefinition.supportsStep(step)) {
                val currentLongestPattern = getPatternByDefinition(definitionsByClass[stepDefinition.javaClass])
                val newPattern = getPatternByDefinition(stepDefinition)
                val newPatternLength = newPattern?.pattern()?.length ?: -1
                if (currentLongestPattern == null || currentLongestPattern.pattern().length < newPatternLength) {
                    definitionsByClass[stepDefinition.javaClass] = stepDefinition
                }
            }
        }
        return definitionsByClass.values
    }

    /**
     * Returns pattern from step definition (if exists)
     *
     * @param definition step definition
     * @return pattern or null if does not exist
     */
    private fun getPatternByDefinition(definition: AbstractStepDefinition?): Pattern? {
        return definition?.getPattern()
    }

    private fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        val result: ArrayList<AbstractStepDefinition> = ArrayList<AbstractStepDefinition>()
        for (extension in cucumberExtensions) {
            result.addAll(org.skellig.plugin.language.feature.SkelligUtil.loadFrameworkSteps(extension, featureFile, module))
        }
        return result
    }

    val cucumberExtensions: List<SkelligExtensionPoint>
        get() = SkelligExtensionPoint.EP_NAME.extensionList

    fun getExtensionCount(): Int {
        return cucumberExtensions.size
    }

}