package org.skellig.plugin.language.feature.steps.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.feature.psi.GherkinFile
import org.skellig.plugin.language.feature.psi.impl.GherkinStepImpl
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module

interface SkelligExtensionPoint {
    companion object {
        @JvmField
        val EP_NAME = ExtensionPointName.create<SkelligExtensionPoint>("org.skellig.plugin.language.skelligExtensionPoint")
    }

    /**
     * Checks if the child could be step definition file
     * @param child a PsiFile
     * @param parent container of the child
     * @return true if the child could be step definition file, else otherwise
     */
    fun isStepLikeFile(child: PsiElement, parent: PsiElement): Boolean

    /**
     * Checks if the child could be a step definition container
     * @param child PsiElement to check
     * @param parent it's container
     * @return true if child could be step definition container and it's possible to write in it
     */
    fun isWritableStepLikeFile(child: PsiElement, parent: PsiElement): Boolean

    /**
     * Provides type of step definition file
     * @return type
     */
    val stepFileType: BDDFrameworkType

    /**
     * Provides all possible step definitions available from current feature file.
     * @param featureFile
     * @param module
     * @return
     */
    fun loadStepsFor(featureFile: PsiFile?, module: Module): List<AbstractStepDefinition>

    fun getStepDefinitionContainers(file: GherkinFile): Collection<PsiFile?>?

    fun isGherkin6Supported(module: Module): Boolean {
        return false
    }

    fun getStepName(step: PsiElement): String? {
        return if (step !is GherkinStepImpl) {
            null
        } else step.substitutedName
    }
}