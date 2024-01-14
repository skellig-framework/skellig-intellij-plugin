package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.FormatterUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepAllValues
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepBlock(private val node: ASTNode) : Block {

    override fun getTextRange(): TextRange {
        return node.textRange
    }

    override fun getSubBlocks(): MutableList<Block> {
        val children = node.getChildren(null)
        if (children.isEmpty()) {
            return mutableListOf()
        }
        val blocks = mutableListOf<Block>()
        for (child in children) {
            if (child.elementType != TokenType.WHITE_SPACE && child.elementType != SkelligTestStepTypes.NEWLINE) {
                blocks.add(SkelligTestStepBlock(child))
            }
        }
        return blocks
    }

    override fun getWrap(): Wrap? {
        return null
    }

    override fun getIndent(): Indent? {
        return if (isInsideBracesOrBrackets()) Indent.getNormalIndent()
        else Indent.getNoneIndent()
    }

    override fun getAlignment(): Alignment? {
        return null
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        if (child1 is SkelligTestStepBlock && child2 is SkelligTestStepBlock) {

            val elementType1 = child1.node.elementType
            val elementType2 = child2.node.elementType

            if (elementType1 == SkelligTestStepTypes.VALUE_ASSIGN ||
                elementType2 == SkelligTestStepTypes.VALUE_ASSIGN ||
                elementType2 == SkelligTestStepTypes.MAP ||
                elementType2 == SkelligTestStepTypes.ARRAY ||
                elementType2 == SkelligTestStepTypes.ARRAY ||
                elementType2 == SkelligTestStepTypes.OBJECT_L_BRACKET) {
                return Spacing.createSpacing(1, 1, 0, false, 0)
            }
        }
        return null
    }

    override fun getChildAttributes(p0: Int): ChildAttributes {
        return ChildAttributes(null, this.getFirstChildAlignment())
    }

    private fun getFirstChildAlignment(): Alignment? {
        return null
    }

    override fun isIncomplete(): Boolean {
        return FormatterUtil.isIncomplete(node);
    }

    override fun isLeaf(): Boolean {
        return node.firstChildNode == null
    }

    private fun isInsideBracesOrBrackets(): Boolean {
//        return node.psi is SkelligTestStepPair &&
//                ((node.psi as SkelligTestStepPair).value != null || (node.psi as SkelligTestStepPair).map != null)
        return node.psi is SkelligTestStepPair || node.psi is SkelligTestStepAllValues
    }

}