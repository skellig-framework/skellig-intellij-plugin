// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

import com.intellij.lang.ASTNode
import com.intellij.pom.PomTarget
import com.intellij.psi.PsiNamedElement
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition

interface GherkinStep : GherkinPsiElement, GherkinSuppressionHolder, PomTarget, PsiNamedElement {
    val keyword: ASTNode?
    override fun getName(): String
    val table: GherkinTable?
    val pystring: GherkinPystring?
    val stepHolder: GherkinStepsHolder?

    /**
     * @return List with not empty unique possible substitutions names
     */
    val paramsSubstitutions: List<String>
    val substitutedName: String?

    /**
     * @return all step definitions (may be heavy). Works just like [org.jetbrains.plugins.cucumber.steps.reference.CucumberStepReference.resolveToDefinition]
     * @see org.jetbrains.plugins.cucumber.steps.reference.CucumberStepReference.resolveToDefinition
     */
    fun findDefinitions(): Collection<AbstractStepDefinition>

    /**
     * Checks if step can be renamed (actually, all definitions are asked).
     * See [AbstractStepDefinition.supportsRename].
     *
     * @param newName new name (to check if renaming to it is supported) or null to check if step could be renamed at all.
     * Steps with out of defintiions can't be renamed as well.
     * @return true it could be
     */
    fun isRenameAllowed(newName: String?): Boolean

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinStep>(0)
    }
}