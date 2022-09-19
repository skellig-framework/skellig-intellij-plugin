package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NonNls
import org.skellig.plugin.language.feature.psi.GherkinElementFactory
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinStepParameter

class GherkinStepParameterImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinStepParameter {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitStepParameter(this)
    }

    override fun toString(): String {
        return "GherkinStepParameter:$text"
    }

    override fun setName(@NonNls name: String): PsiElement {
        val content: LeafPsiElement? = PsiTreeUtil.getChildOfType(this, LeafPsiElement::class.java)
        val elements: Array<PsiElement> = GherkinElementFactory.getTopLevelElements(project, name)
        node.replaceChild(content!!, elements[0].getNode())
        return this
    }

    override fun getReference(): PsiReference {
        return GherkinStepParameterReference(this)
    }

    override fun getName(): String? {
        return text
    }

    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    override fun getUseScope(): SearchScope {
        return LocalSearchScope(containingFile)
    }
}

