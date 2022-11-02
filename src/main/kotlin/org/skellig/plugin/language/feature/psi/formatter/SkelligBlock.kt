package org.skellig.plugin.language.feature.psi.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.tree.TreeUtil
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.feature.psi.SkelligElementTypes
import org.skellig.plugin.language.feature.psi.SkelligTable
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes

class SkelligBlock @JvmOverloads constructor(
    private val myNode: ASTNode,
    private val myIndent: Indent? = Indent.getAbsoluteNoneIndent(),
    private val myTextRange: TextRange = myNode.textRange,
    private val myLeaf: Boolean = false
) : ASTBlock {

    private var myChildren: List<Block>? = null

    override fun getNode(): ASTNode? {
        return myNode
    }

    override fun getTextRange(): TextRange {
        return myTextRange
    }

    override fun getSubBlocks(): List<Block> {
        if (myLeaf) return emptyList()
        if (myChildren == null) {
            myChildren = buildChildren()
        }
        return myChildren!!
    }

    private fun buildChildren(): List<Block> {
        val children = myNode.getChildren(null)
        if (children.size == 0) {
            return emptyList()
        }
        val result: MutableList<Block> = ArrayList()
        for (child in children) {
            if (child.elementType === TokenType.WHITE_SPACE) {
                continue
            }
            val isTagInsideScenario =
                child.elementType === SkelligElementTypes.TAG && myNode.elementType === SkelligElementTypes.SCENARIO && child.startOffset > myNode.startOffset
            var indent: Indent?
            indent = if (BLOCKS_TO_INDENT.contains(child.elementType) || isTagInsideScenario) {
                Indent.getNormalIndent()
            } else {
                Indent.getNoneIndent()
            }
            // skip epmty cells
            if (child.elementType === SkelligElementTypes.TABLE_CELL) {
                if (child.getChildren(null).size == 0) {
                    continue
                }
            }
            if (child.elementType === SkelligTokenTypes.COMMENT) {
                val commentIndentElement = child.treePrev
                if (commentIndentElement != null && (commentIndentElement.text.contains("\n") || commentIndentElement.treePrev == null)) {
                    val whiteSpaceText = commentIndentElement.text
                    val lineBreakIndex = whiteSpaceText.lastIndexOf("\n")
                    indent = Indent.getSpaceIndent(whiteSpaceText.length - lineBreakIndex - 1)
                }
            }
            result.add(SkelligBlock(child, indent))
        }
        return result
    }

    override fun getWrap(): Wrap? {
        return null
    }

    override fun getIndent(): Indent? {
        return myIndent
    }

    override fun getAlignment(): Alignment? {
        return null
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        if (child1 == null) {
            return null
        }
        val block1 = child1 as ASTBlock
        val block2 = child2 as ASTBlock
        val node1 = block1.node
        val node2 = block2.node
        val parent1 = if (node1!!.treeParent != null) node1.treeParent.elementType else null
        val elementType1 = node1.elementType
        val elementType2 = node2!!.elementType
        if (READ_ONLY_BLOCKS.contains(elementType2)) {
            return Spacing.getReadOnlySpacing()
        }
        if (SkelligElementTypes.SCENARIOS.contains(elementType2) && elementType1 !== SkelligTokenTypes.COMMENT && parent1 !== SkelligElementTypes.RULE) {
            return Spacing.createSpacing(0, 0, 2, true, 2)
        }
        if (elementType1 === SkelligTokenTypes.PIPE &&
            elementType2 === SkelligElementTypes.TABLE_CELL
        ) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        if ((elementType1 === SkelligElementTypes.TABLE_CELL || elementType1 === SkelligTokenTypes.PIPE) &&
            elementType2 === SkelligTokenTypes.PIPE
        ) {
            val tableNode = TreeUtil.findParent(node1, SkelligElementTypes.TABLE)
            if (tableNode != null) {
                val columnIndex = getTableCellColumnIndex(node1)
                val maxWidth = (tableNode.psi as SkelligTable).getColumnWidth(columnIndex)
                var spacingWidth = maxWidth - node1.text.trim { it <= ' ' }.length + 1
                if (elementType1 === SkelligTokenTypes.PIPE) {
                    spacingWidth += 2
                }
                return Spacing.createSpacing(spacingWidth, spacingWidth, 0, false, 0)
            }
        }
        return null
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val childIndent = if (BLOCKS_TO_INDENT_CHILDREN.contains(node!!.elementType)) Indent.getNormalIndent() else Indent.getNoneIndent()
        return ChildAttributes(childIndent, null)
    }

    override fun isIncomplete(): Boolean {
        if (SkelligElementTypes.SCENARIOS.contains(node!!.elementType)) {
            return true
        }
        return if (node!!.elementType === SkelligElementTypes.FEATURE) {
            node!!.getChildren(
                TokenSet.create(
                    SkelligElementTypes.FEATURE_HEADER,
                    SkelligElementTypes.SCENARIO
                )
            ).size == 0
        } else false
    }

    override fun isLeaf(): Boolean {
        return myLeaf || subBlocks.size == 0
    }

    companion object {
        private val BLOCKS_TO_INDENT = TokenSet.create(
            SkelligElementTypes.FEATURE_HEADER,
            SkelligElementTypes.RULE,
            SkelligElementTypes.SCENARIO,
            SkelligElementTypes.STEP,
            SkelligElementTypes.TABLE,
            SkelligElementTypes.EXAMPLES_BLOCK
        )
        private val BLOCKS_TO_INDENT_CHILDREN = TokenSet.create(
            SkelligElementTypes.SKELLIG_FILE,
            SkelligElementTypes.FEATURE,
            SkelligElementTypes.RULE,
            SkelligElementTypes.SCENARIO
        )
        private val READ_ONLY_BLOCKS = TokenSet.create(SkelligElementTypes.PYSTRING, SkelligTokenTypes.COMMENT)
        private fun getTableCellColumnIndex(node: ASTNode?): Int {
            var astNode = node
            var pipeCount = 0
            while (astNode != null) {
                if (astNode.elementType === SkelligTokenTypes.PIPE) {
                    pipeCount++
                }
                astNode = astNode.treePrev
            }
            return pipeCount - 1
        }
    }
}