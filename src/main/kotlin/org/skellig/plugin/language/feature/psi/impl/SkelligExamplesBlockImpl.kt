package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.feature.psi.SkelligElementTypes
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligExamplesBlock
import org.skellig.plugin.language.feature.psi.SkelligTable

class SkelligExamplesBlockImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligExamplesBlock {
    override fun toString(): String {
        return "SkelligExamplesBlock:$elementText"
    }

    override fun getPresentableText(): String? {
        return buildPresentableText("Examples")
    }

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitExamplesBlock(this)
    }

    override fun getTable(): SkelligTable? {
        val node = node
        val tableNode = node.findChildByType(TABLE_FILTER)
        return if (tableNode == null) null else tableNode.psi as SkelligTable
    }

    companion object {
        private val TABLE_FILTER = TokenSet.create(SkelligElementTypes.Companion.TABLE)
    }
}