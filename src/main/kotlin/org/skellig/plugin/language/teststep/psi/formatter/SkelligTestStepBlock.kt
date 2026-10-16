package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.FormatterUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepBlock(
    private val node: ASTNode,
    private val indent: Indent = Indent.getNoneIndent(),
) : Block {
    private val children: MutableList<Block> by lazy {
        node
            .getChildren(null)
            .filter { it.elementType != TokenType.WHITE_SPACE && it.elementType != SkelligTestStepTypes.NEWLINE && it.text.isNotBlank() }
            .mapTo(mutableListOf()) { SkelligTestStepBlock(it, childIndent(it)) }
    }

    private val range: TextRange by lazy {
        // ARG and ARRAY nodes can own leading/trailing NEWLINE tokens. Those
        // belong to the gap between formatting blocks, not to the argument itself.
        if (node.firstChildNode == null) {
            node.textRange
        } else {
            val text = node.text
            val start = text.indexOfFirst { !it.isWhitespace() }
            val end = text.indexOfLast { !it.isWhitespace() }
            if (start < 0) node.textRange else TextRange(node.startOffset + start, node.startOffset + end + 1)
        }
    }

    override fun getTextRange(): TextRange = range

    override fun getSubBlocks(): MutableList<Block> = children

    override fun getWrap(): Wrap? = null

    override fun getIndent(): Indent = indent

    override fun getAlignment(): Alignment? = null

    override fun getSpacing(
        child1: Block?,
        child2: Block,
    ): Spacing? {
        if (child1 !is SkelligTestStepBlock || child2 !is SkelligTestStepBlock) return null
        val left = child1.node.elementType
        val right = child2.node.elementType
        return when {
            left == SkelligTestStepTypes.COMMENT -> spacing(0, lineFeeds = 1)
            right == SkelligTestStepTypes.COMMENT -> spacing(1)
            left == SkelligTestStepTypes.VALUE_ASSIGN || right == SkelligTestStepTypes.VALUE_ASSIGN -> spacing(1)
            right == SkelligTestStepTypes.COMMA -> spacing(0, keepLineBreaks = false)
            left == SkelligTestStepTypes.COMMA -> spacing(1)
            child1.node.text == "." || child2.node.text == "." -> spacing(0, keepLineBreaks = false)
            right == SkelligTestStepTypes.MAP || right == SkelligTestStepTypes.ARRAY -> spacing(1)
            right == SkelligTestStepTypes.OBJECT_L_BRACKET -> spacing(1)
            right == SkelligTestStepTypes.FUNCTION_L_BRACKET ->
                spacing(if (node.elementType == SkelligTestStepTypes.TEST_STEP_NAME_EXPRESSION) 1 else 0)
            left == SkelligTestStepTypes.FUNCTION_L_BRACKET || right == SkelligTestStepTypes.FUNCTION_R_BRACKET -> spacing(0)
            left == SkelligTestStepTypes.REFERENCE_BRACKET ||
                (node.elementType == SkelligTestStepTypes.REFERENCE_EXPRESSION && right == SkelligTestStepTypes.OBJECT_R_BRACKET) ->
                spacing(0)
            left == SkelligTestStepTypes.ARRAY_L_BRACKET || right == SkelligTestStepTypes.ARRAY_R_BRACKET -> spacing(0)
            left == SkelligTestStepTypes.OBJECT_L_BRACKET && right == SkelligTestStepTypes.OBJECT_R_BRACKET -> spacing(0)
            left == SkelligTestStepTypes.OBJECT_L_BRACKET || right == SkelligTestStepTypes.OBJECT_R_BRACKET -> spacing(1)
            left == SkelligTestStepTypes.PAIR && right == SkelligTestStepTypes.PAIR -> spacing(0, lineFeeds = 1)
            else -> null
        }
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val previous = children.getOrNull(newChildIndex - 1) as? SkelligTestStepBlock
        val afterClosingDelimiter = previous?.node?.elementType in CLOSING_DELIMITERS
        val indent =
            when {
                afterClosingDelimiter -> Indent.getNoneIndent()
                node.elementType in OBJECT_CONTAINERS || node.elementType == SkelligTestStepTypes.ARRAY -> Indent.getNormalIndent()
                node.elementType == SkelligTestStepTypes.FUNCTION_EXPRESSION -> Indent.getContinuationIndent()
                else -> Indent.getNoneIndent()
            }
        return ChildAttributes(indent, null)
    }

    override fun isIncomplete(): Boolean = FormatterUtil.isIncomplete(node)

    override fun isLeaf(): Boolean = children.isEmpty()

    private fun childIndent(child: ASTNode): Indent =
        when {
            child.elementType in OPENING_DELIMITERS || child.elementType in CLOSING_DELIMITERS -> Indent.getNoneIndent()
            node.elementType in OBJECT_CONTAINERS &&
                (child.elementType == SkelligTestStepTypes.PAIR || child.elementType == SkelligTestStepTypes.COMMENT) ->
                Indent.getNormalIndent()
            node.elementType == SkelligTestStepTypes.ARRAY &&
                (child.elementType == SkelligTestStepTypes.ALL_VALUES || child.elementType == SkelligTestStepTypes.COMMENT) ->
                Indent.getNormalIndent()
            node.elementType == SkelligTestStepTypes.FUNCTION_EXPRESSION &&
                (child.elementType == SkelligTestStepTypes.ARG || child.elementType == SkelligTestStepTypes.COMMENT) ->
                Indent.getContinuationIndent()
            else -> Indent.getNoneIndent()
        }

    private fun spacing(
        spaces: Int,
        lineFeeds: Int = 0,
        keepLineBreaks: Boolean = true,
    ): Spacing = Spacing.createSpacing(spaces, spaces, lineFeeds, keepLineBreaks, 2)

    companion object {
        private val OBJECT_CONTAINERS = setOf(SkelligTestStepTypes.MAP, SkelligTestStepTypes.TEST_STEP_NAME_EXPRESSION)
        private val OPENING_DELIMITERS =
            setOf(SkelligTestStepTypes.OBJECT_L_BRACKET, SkelligTestStepTypes.ARRAY_L_BRACKET, SkelligTestStepTypes.FUNCTION_L_BRACKET)
        private val CLOSING_DELIMITERS =
            setOf(SkelligTestStepTypes.OBJECT_R_BRACKET, SkelligTestStepTypes.ARRAY_R_BRACKET, SkelligTestStepTypes.FUNCTION_R_BRACKET)
    }
}
