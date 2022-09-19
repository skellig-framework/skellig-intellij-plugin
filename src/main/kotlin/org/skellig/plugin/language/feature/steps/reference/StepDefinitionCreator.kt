package org.skellig.plugin.language.feature.steps.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.feature.psi.GherkinStep

interface StepDefinitionCreator {
    /**
     * Creates step definition file
     * @param dir where to create file
     * @param name of created file
     * @return PsiFile object of created file
     */
    fun createStepDefinitionContainer(dir: PsiDirectory, name: String): PsiFile

    /**
     * Creates step definition
     * @param step to implement
     * @param file where to create step definition
     * @param withTemplate should or not run template builder around regex and step body. We should not force user to go through
     * number of templates in case of "Create All Step Definitions" action invoked
     * @return true if success, false otherwise
     */
    fun createStepDefinition(step: GherkinStep, file: PsiFile, withTemplate: Boolean): Boolean {
        return false
    }

    /**
     * Validates name of new step definition file
     * @param fileName name of file to check
     * @return true if name is valid, false otherwise
     */
    fun validateNewStepDefinitionFileName(project: Project, fileName: String): Boolean {
        return true
    }

    fun getDefaultStepDefinitionFolderPath(step: GherkinStep): String

    /**
     * @return step definition file path relative to step definition folder
     */
    fun getStepDefinitionFilePath(psiFile: PsiFile): String

    /**
     * Provides default name of step definition file
     * @param step step we want to create definition container for
     * @return String representing default name of step definition file
     */
    fun getDefaultStepFileName(step: GherkinStep): String
}