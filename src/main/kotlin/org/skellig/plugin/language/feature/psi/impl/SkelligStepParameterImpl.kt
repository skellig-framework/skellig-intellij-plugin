package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NonNls
import org.skellig.plugin.language.feature.psi.SkelligElementFactory
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligStepParameter

class SkelligStepParameterImpl(
    node: ASTNode,
) : SkelligPsiElementBase(node),
    SkelligStepParameter {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitStepParameter(this)
    }

    override fun toString(): String = "SkelligStepParameter:$text"

    override fun setName(
        @NonNls name: String,
    ): PsiElement {
        val content: LeafPsiElement? = PsiTreeUtil.getChildOfType(this, LeafPsiElement::class.java)
        val elements: Array<PsiElement> = SkelligElementFactory.getTopLevelElements(project, name)
        node.replaceChild(content!!, elements[0].getNode())
        return this
    }

    override fun getReference(): PsiReference = SkelligStepParameterReference(this)

    override fun getName(): String? = text

    override fun getNameIdentifier(): PsiElement? = this

    override fun getUseScope(): SearchScope = LocalSearchScope(containingFile)
}
