package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepBlock @JvmOverloads constructor(
    private val myNode: ASTNode,
    private val myIndent: Indent? = Indent.getAbsoluteNoneIndent(),
    private val myTextRange: TextRange = myNode.textRange,
    private val myLeaf: Boolean = false
) : ASTBlock {

    companion object {
        private val BLOCKS_TO_INDENT = TokenSet.create(
            SkelligTestStepTypes.MAP,
            SkelligTestStepTypes.ARRAY,
        )

        private val BLOCKS_TO_INDENT_CHILDREN = TokenSet.create(

        )

        private val BRACKET_TO_TEXT_BLOCKS_TO_SPACE = TokenSet.create(
            SkelligTestStepTypes.EQUAL
        )

        private val TEXT_TO_BRACKET_BLOCKS_TO_SPACE = TokenSet.create(
            SkelligTestStepTypes.EQUAL,
            SkelligTestStepTypes.OBJECT_L_BRACKET,
            SkelligTestStepTypes.OBJECT_R_BRACKET,
            SkelligTestStepTypes.ARRAY_L_BRACKET,
            SkelligTestStepTypes.ARRAY_R_BRACKET
        )

        private val TEXT_BLOCKS_TO_SPACE = TokenSet.create(
            SkelligTestStepTypes.NAME,
            SkelligTestStepTypes.VALUE_SYMBOLS,
            SkelligTestStepTypes.KEY,
            SkelligTestStepTypes.VALUE
        )

        private val READ_ONLY_BLOCKS = TokenSet.create(SkelligTestStepTypes.STRING)
    }

    private var myChildren: List<Block>? = null

    override fun getNode(): ASTNode {
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
        if (children.isEmpty()) {
            return emptyList()
        }
        val result: MutableList<Block> = ArrayList()
        for (child in children) {
            if (child.elementType === TokenType.WHITE_SPACE || child.elementType === TokenType.NEW_LINE_INDENT) {
                continue
            }

            val indent = if (
                child.elementType == SkelligTestStepTypes.ID ||
                child.elementType == SkelligTestStepTypes.MAP ||
                child.elementType == SkelligTestStepTypes.VALUE ||
                child.elementType == SkelligTestStepTypes.ARRAY
            ) {
                Indent.getNormalIndent()
            } else Indent.getNoneIndent()
            result.add(SkelligTestStepBlock(child, indent))
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
        val elementType1 = block1.node?.elementType
        val elementType2 = block2.node?.elementType

        if (READ_ONLY_BLOCKS.contains(elementType2)) {
            return Spacing.getReadOnlySpacing()
        }

        if (TEXT_TO_BRACKET_BLOCKS_TO_SPACE.contains(elementType2) ||
            BRACKET_TO_TEXT_BLOCKS_TO_SPACE.contains(elementType1) ||
            (elementType1 == SkelligTestStepTypes.KEY && elementType2 == SkelligTestStepTypes.ARRAY) ||
            (elementType1 == SkelligTestStepTypes.KEY && elementType2 == SkelligTestStepTypes.MAP) ||
            (elementType1 == SkelligTestStepTypes.NAME && elementType2 == SkelligTestStepTypes.OBJECT_L_BRACKET)) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }

        return null
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val childIndent = if (BLOCKS_TO_INDENT_CHILDREN.contains(node.elementType)) Indent.getNormalIndent() else Indent.getNoneIndent()
        return ChildAttributes(childIndent, null)
    }

    override fun isIncomplete(): Boolean {
        return false
    }

    override fun isLeaf(): Boolean {
        return myLeaf || subBlocks.isEmpty()
    }

}