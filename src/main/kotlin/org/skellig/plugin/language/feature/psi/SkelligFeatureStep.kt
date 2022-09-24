package org.skellig.plugin.language.feature.psi

import com.intellij.lang.ASTNode
import com.intellij.pom.PomTarget
import com.intellij.psi.PsiNamedElement
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

interface SkelligFeatureStep : SkelligPsiElement, SkelligSuppressionHolder, PomTarget, PsiNamedElement {
    val keyword: ASTNode?
    override fun getName(): String
    val table: SkelligTable?
    val pystring: SkelligPystring?
    val stepHolder: SkelligStepsHolder?


    val paramsSubstitutions: List<String>
    val substitutedName: String?

    fun findDefinitions(): Collection<AbstractStepDefinition>

    fun isRenameAllowed(newName: String?): Boolean

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<SkelligFeatureStep>(0)
    }
}