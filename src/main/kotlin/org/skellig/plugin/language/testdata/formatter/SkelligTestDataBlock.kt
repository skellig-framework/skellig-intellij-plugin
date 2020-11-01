package org.skellig.plugin.language.testdata.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.psi.*
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTokenType
import java.util.*


class SkelligTestDataBlock(@NotNull node: ASTNode, @Nullable wrap: Wrap?, @Nullable alignment: Alignment?,
                           private val spacingBuilder: SpacingBuilder?) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block>? {
        val blocks: MutableList<Block> = ArrayList<Block>()
        var child: ASTNode? = myNode.firstChildNode
        while (child != null) {
            if (child.elementType !== TokenType.WHITE_SPACE) {
                val block: Block = SkelligTestDataBlock(child, Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(), spacingBuilder)
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getIndent(): Indent? {
        if (isElementWithBrackets(node.psi)) {
            return Indent.getNormalIndent(true)
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

    private fun isElementWithBrackets(element: PsiElement?): Boolean {
        return element is SkelligTestDataJsonFunctionsDef ||
                element is SkelligTestDataComplexFunctionsDef ||
                element is SkelligTestDataArrayDef ||
                element is SkelligTestDataObjectDefinition ||
                element is SkelligTestDataAnonymousObjectDefinition ||
                element is SkelligTestDataVariablesDef ||
                element is SkelligTestDataDataDef ||
                element is SkelligTestDataValidationDef ||
                element is SkelligTestDataFieldDefinition
    }
}