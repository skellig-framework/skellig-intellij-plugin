package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligTableCell
import org.skellig.plugin.language.feature.psi.SkelligTableRow
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes
import java.util.*

open class SkelligTableRowImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligTableRow {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitTableRow(this)
    }

    override fun toString(): String {
        return "SkelligTableRow"
    }

    override val psiCells: List<SkelligTableCell>
        get() = getChildrenByFilter(this, SkelligTableCell::class.java)

    override fun getColumnWidth(columnIndex: Int): Int {
        val cells = psiCells
        if (cells.size <= columnIndex) {
            return 0
        }
        val cell: PsiElement = cells[columnIndex]
        return if (cell.text != null) {
            cell.text.trim { it <= ' ' }.length
        } else 0
    }

    override fun deleteCell(columnIndex: Int) {
        val cells = psiCells
        if (columnIndex < cells.size) {
            val cell: PsiElement = cells[columnIndex]
            var nextPipe = cell.nextSibling
            if (nextPipe is PsiWhiteSpace) {
                nextPipe = nextPipe.getNextSibling()
            }
            if (nextPipe != null && nextPipe.node.elementType === SkelligTokenTypes.Companion.PIPE) {
                nextPipe.delete()
            }
            cell.delete()
        }
    }

    companion object {
        fun <T : PsiElement?> getChildrenByFilter(psiElement: PsiElement, c: Class<T>): List<T> {
            val list = LinkedList<T>()
            for (element in psiElement.children) {
                if (c.isInstance(element)) {
                    list.add(element as T)
                }
            }
            return if (list.isEmpty()) emptyList() else list
        }
    }
}