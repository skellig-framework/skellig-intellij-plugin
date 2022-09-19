package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.feature.psi.GherkinElementTypes
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinExamplesBlock
import org.skellig.plugin.language.feature.psi.GherkinTable

class GherkinExamplesBlockImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinExamplesBlock {
    override fun toString(): String {
        return "GherkinExamplesBlock:$elementText"
    }

    override fun getPresentableText(): String? {
        return buildPresentableText("Examples")
    }

    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitExamplesBlock(this)
    }

    override fun getTable(): GherkinTable? {
        val node = node
        val tableNode = node.findChildByType(TABLE_FILTER)
        return if (tableNode == null) null else tableNode.psi as GherkinTable
    }

    companion object {
        private val TABLE_FILTER = TokenSet.create(GherkinElementTypes.Companion.TABLE)
    }
}