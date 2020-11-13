package org.skellig.plugin.language.feature.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.feature.psi.SkelligFeatureScenarioDef
import org.skellig.plugin.language.feature.psi.SkelligFeatureTable
import java.util.*


class SkelligFeatureBlock(@NotNull node: ASTNode, @Nullable wrap: Wrap?, @Nullable alignment: Alignment?,
                          private val spacingBuilder: SpacingBuilder?) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block>? {
        val blocks: MutableList<Block> = ArrayList<Block>()
        var child: ASTNode? = myNode.firstChildNode
        while (child != null) {
            if (child.elementType !== TokenType.WHITE_SPACE) {
                val block: Block = SkelligFeatureBlock(child, Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(), spacingBuilder)
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getIndent(): Indent? {
        if (node.psi is SkelligFeatureScenarioDef) {
            return Indent.getNormalIndent(true)
        } else if (node.psi is SkelligFeatureTable) {
            return Indent.getIndent(Indent.Type.SPACES, 2, false, false);
        }
        return Indent.getNoneIndent()
    }

    @Nullable
    override fun getSpacing(@Nullable child1: Block?, @NotNull child2: Block): Spacing? {
        return spacingBuilder!!.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean {
        return myNode.firstChildNode == null
    }

}