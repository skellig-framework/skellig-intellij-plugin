package org.skellig.plugin.language.feature.psi

import com.intellij.lang.ASTNode
import com.intellij.pom.PomTarget
import com.intellij.psi.PsiNamedElement
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

interface GherkinStep : GherkinPsiElement, GherkinSuppressionHolder, PomTarget, PsiNamedElement {
    val keyword: ASTNode?
    override fun getName(): String
    val table: GherkinTable?
    val pystring: GherkinPystring?
    val stepHolder: GherkinStepsHolder?


    val paramsSubstitutions: List<String>
    val substitutedName: String?

    fun findDefinitions(): Collection<AbstractStepDefinition>

    fun isRenameAllowed(newName: String?): Boolean

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinStep>(0)
    }
}