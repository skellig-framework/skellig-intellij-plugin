package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import org.jetbrains.plugins.cucumber.psi.GherkinElementTypes
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor
import org.jetbrains.plugins.cucumber.psi.GherkinExamplesBlock
import org.jetbrains.plugins.cucumber.psi.GherkinTable

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