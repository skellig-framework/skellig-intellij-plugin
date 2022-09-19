package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.GherkinElementFactory
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinTableCell

class GherkinTableCellImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinTableCell {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitGherkinTableCell(this)
    }

    override fun getPresentableText(): String? {
        return String.format("Step parameter '%s'", name)
    }

    override fun getReference(): PsiReference? {
        return GherkinSimpleReference(this);
    }

    override fun getName(): String? {
        return text
    }

    override fun setName(name: String): PsiElement {
        val content: LeafPsiElement? = PsiTreeUtil.getChildOfType(this, LeafPsiElement::class.java)
        if (content != null) {
            val elements: Array<PsiElement> = GherkinElementFactory.getTopLevelElements(project, name)
            node.replaceChild(content, elements[0].getNode())
        }
        return this
    }

    override fun getNameIdentifier(): PsiElement? = PsiTreeUtil.getChildOfType(this, LeafPsiElement::class.java)

    override fun getUseScope(): SearchScope {
        return LocalSearchScope(containingFile)
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                referencesInner, this
            )
        }
    }

    private val referencesInner: Array<PsiReference>
        private get() = ReferenceProvidersRegistry.getReferencesFromProviders(this)
}