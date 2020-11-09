package org.skellig.plugin.language.feature.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.util.elementType
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.feature.psi.*
import java.util.*


class SkelligFeatureBlock(@NotNull node: ASTNode, @Nullable wrap: Wrap?, @Nullable alignment: Alignment?,
                          private val spacingBuilder: SpacingBuilder?) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block>? {
        val blocks: MutableList<Block> = ArrayList<Block>()
        var child: ASTNode? = myNode.firstChildNode
        while (child != null) {
            if (child.elementType !== TokenType.WHITE_SPACE ) {
//            if (child.psi is SkelligFeatureFeature ||
//                    child.psi is SkelligFeatureTags ||
//                    child.psi is SkelligFeatureScenario ||
//                    child.psi is SkelligFeatureStep) {
                val block: Block = SkelligFeatureBlock(child, Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(true), spacingBuilder)
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getIndent(): Indent? {
        if (node.psi.textRange.length > 0 &&
                (node.psi is SkelligFeatureScenario ||
                node.psi is SkelligFeatureSteps ||
                node.psi is SkelligFeatureDataSections ||
                node.psi is SkelligFeatureTags)) {
//        if (node.psi.elementType == SkelligFeatureTypes.SCENARIO ||
//                node.psi.elementType == SkelligFeatureTypes.STEPS) {
            return Indent.getNormalIndent(false)
//            return Indent.getIndent(Indent.Type.SPACES, 1, false, true)
        }
        return Indent.getNoneIndent()
    }

    @Nullable
    override fun getSpacing(@Nullable child1: Block?, @NotNull child2: Block): Spacing? {
        return null//spacingBuilder!!.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean {
        return false//myNode.firstChildNode == null
    }

}