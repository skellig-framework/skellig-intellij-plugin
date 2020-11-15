package org.skellig.plugin.language.feature.formatter

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.elementType
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.feature.psi.SkelligFeatureTypes
import java.util.*
import java.util.stream.Collectors

internal class TableFormatter {

    fun format(element : PsiElement){
        val editor = FileEditorManager.getInstance(element.project).selectedTextEditor
        formatParameterSeparator(element, editor!!, PsiFileFactoryImpl(element.project), Offset())
        PsiDocumentManager.getInstance(element.project).commitDocument(editor.document);
    }

    private fun formatParameterSeparator(element: PsiElement, editor: @Nullable Editor, fileFactory: PsiFileFactoryImpl, offset: Offset) {
        element.children.forEach { node ->
            if (node.elementType == SkelligFeatureTypes.TABLE) {
                val textRange = node.textRange
                val formatTable = formatTable(node)
                val rangeLength = textRange.endOffset - textRange.startOffset
                val newTextLength = formatTable.length

                if (offset.value == Int.MAX_VALUE) {
                    offset.value = 0
                }

                editor.document.replaceString(textRange.startOffset + offset.value, textRange.endOffset + offset.value, formatTable)

                offset.value += newTextLength - rangeLength
            }
            formatParameterSeparator(node, editor, fileFactory, offset)
        }
    }

    private fun formatTable(node: @NotNull PsiElement): String {

        val table = node.text.split("\n")
                .map { line -> line.split("|").toMutableList() }
                .toMutableList()

        if (table.maxBy { i -> i.size } == table.minBy { i -> i.size }) {
            val sizes: MutableList<Int> = ArrayList()
            for (j in table[0].indices) {
                for (i in table.indices) {
                    sizes.add(table[i][j].trim { it <= ' ' }.length)
                }
                val maxLength = sizes.max()
                for (i in table.indices) {
                    val element = table[i][j].trim()
                    table[i][j] = element + " ".repeat(maxLength!! - element.length)
                }
                sizes.clear()
            }

            return table.stream()
                    .map { row -> row.joinToString(separator = "|") }
                    .collect(Collectors.joining("\n"))
        } else {
            return node.text
        }
    }

    data class Offset(var value: Int = Int.MAX_VALUE)

}