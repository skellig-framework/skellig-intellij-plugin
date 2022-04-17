package org.skellig.plugin.language.testdata

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
import org.skellig.plugin.language.testdata.psi.*
import java.util.*


class SkelligTestDataFoldingBuilder : FoldingBuilderEx(), DumbAware {

    private val TEST_GROUP_PREFIX = "test-"
    private val GROUP_PREFIX = "group-"

    @NotNull
    override fun buildFoldRegions(@NotNull root: PsiElement, @NotNull document: Document, quick: Boolean): Array<FoldingDescriptor> {
        // Initialize the group of folding regions that will expand/collapse together.
        // Initialize the list of folding regions
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()
        // Get a collection of the literal expressions in the document below root
        val literalExpressions: Collection<SkelligTestDataDefinition> = PsiTreeUtil.findChildrenOfType(root, SkelligTestDataDefinition::class.java)
        // Evaluate the collection
        var counter = 0
        for (literalExpression in literalExpressions) {
            for (testDef in literalExpression.testDefinitionList) {
                val group = FoldingGroup.newGroup(TEST_GROUP_PREFIX + (counter++))
                descriptors.add(FoldingDescriptor(testDef.node,
                        TextRange(testDef.textRange.startOffset + 5, testDef.textRange.endOffset - 1), group))

                testDef.testBody.statementList.forEach { item -> addDescriptor(item, descriptors, 0) }
            }
        }
        return descriptors.toTypedArray()
    }

    private fun addDescriptor(element: PsiElement?, descriptors: MutableList<FoldingDescriptor>, groupNumber: Int) {
        if (element != null) {
            if (isElementWithBrackets(element)) {

                descriptors.add(FoldingDescriptor(element.node,
                        TextRange(element.textRange.startOffset + getIndexOfFirstBracket(element),
                                element.textRange.endOffset - 1), FoldingGroup.newGroup(GROUP_PREFIX + groupNumber)))
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
        val retTxt = "..."
//        if (node.psi is SkelligTestDataTestDefinition) {
//            val nodeElement: SkelligTestDataTestDefinition = node.psi as SkelligTestDataTestDefinition
//            return getFullText(nodeElement)
//        }
        return retTxt
    }

//    private fun getFullText(node: SkelligTestDataTestDefinition): String {
//        return node.valueDefList.joinToString(" ") { item -> item.text }
//    }

    override fun isCollapsedByDefault(@NotNull node: ASTNode): Boolean {
        return true
    }

    private fun getIndexOfFirstBracket(element: PsiElement): Int {
        val text = element.text
        val index = text.indexOfFirst { c -> c == '{' || c == '[' }
        return if (index == -1) return 1 else index + 1

    }

    private fun isElementWithBrackets(element: PsiElement?): Boolean {
        return element is SkelligTestDataJsonFunctionsDef ||
                element is SkelligTestDataComplexFunctionsDef ||
                element is SkelligTestDataArrayDef ||
                element is SkelligTestDataObjectDefinition ||
                element is SkelligTestDataAnonymousObjectDefinition ||
                element is SkelligTestDataVariablesDef ||
                element is SkelligTestDataDataDef ||
                element is SkelligTestDataValidationDef
    }
}
