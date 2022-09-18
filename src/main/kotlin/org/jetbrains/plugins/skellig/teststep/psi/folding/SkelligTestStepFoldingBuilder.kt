package org.jetbrains.plugins.skellig.teststep.psi.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.skellig.teststep.psi.impl.*


class SkelligTestStepFoldingBuilder : FoldingBuilderEx(), DumbAware {

    private val GROUP_PREFIX = "group-"

    @NotNull
    override fun buildFoldRegions(@NotNull root: PsiElement, @NotNull document: Document, quick: Boolean): Array<FoldingDescriptor> {
        // Initialize the group of folding regions that will expand/collapse together.
        // Initialize the list of folding regions
        val descriptors = mutableListOf<FoldingDescriptor>()
        // Get a collection of the literal expressions in the document below root
        val literalExpressions = PsiTreeUtil.findChildrenOfType(root, SkelligTestStep::class.java)
        // Evaluate the collection
        for (literalExpression in literalExpressions) {
            addDescriptor(literalExpression, descriptors, 0)
        }
        return descriptors.toTypedArray()
    }

    private fun addDescriptor(element: PsiElement?, descriptors: MutableList<FoldingDescriptor>, groupNumber: Int) {
        if (element != null) {
            if (isElementWithBrackets(element)) {
                val start = element.textRange.startOffset + getIndexOfFirstBracket(element)
                val end = element.textRange.endOffset - 1
                if (end - start > 0) {
                    descriptors.add(
                        FoldingDescriptor(
                            element.node,
                            TextRange(start, end), FoldingGroup.newGroup(GROUP_PREFIX + groupNumber)
                        )
                    )
                }
            }
            element.children.forEach { e -> addDescriptor(e, descriptors, groupNumber + 1) }
        }
    }

    /**
     * Gets the Simple Language 'value' string corresponding to the 'key'
     *
     * @param node Node corresponding to PsiLiteralExpression containing a string in the format
     * SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR + Key, where Key is
     * defined by the Simple language file.
     */
    @Nullable
    override fun getPlaceholderText(@NotNull node: ASTNode): String {
        return "..."
    }

    override fun isCollapsedByDefault(@NotNull node: ASTNode): Boolean {
        return false
    }

    private fun getIndexOfFirstBracket(element: PsiElement): Int {
        val text = element.text
        val index = text.indexOfFirst { c -> c == '{' || c == '[' }
        return if (index == -1) return 1 else index + 1

    }

    private fun isElementWithBrackets(element: PsiElement?): Boolean {
        return element is SkelligTestStep ||
                element is SkelligTestStepObject ||
                element is SkelligTestStepArray ||
                element is SkelligTestStepVariables ||
                element is SkelligTestStepRequest ||
                element is SkelligTestStepValidation
    }
}
