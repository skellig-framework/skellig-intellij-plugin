package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.SkelligElementTypes
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligTable
import org.skellig.plugin.language.feature.psi.SkelligTableRow

class SkelligTableImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligTable {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitTable(this)
    }

    override val headerRow: SkelligTableRow?
        get() {
            val node = node
            val tableNode = node.findChildByType(HEADER_ROW_TOKEN_SET)
            return if (tableNode == null) null else tableNode.psi as SkelligTableRow
        }
    override val dataRows: List<SkelligTableRow?>
        get() {
            val result: MutableList<SkelligTableRow?> = ArrayList()
            val rows = PsiTreeUtil.getChildrenOfType(this, SkelligTableRow::class.java)
            if (rows != null) {
                for (row in rows) {
                    if (row !is SkelligTableHeaderRowImpl) {
                        result.add(row)
                    }
                }
            }
            return result
        }

    override fun getColumnWidth(columnIndex: Int): Int {
        var result = 0
        val headerRow = headerRow
        if (headerRow != null) {
            result = headerRow.getColumnWidth(columnIndex)
        }
        for (row in dataRows) {
            result = Math.max(result, row!!.getColumnWidth(columnIndex))
        }
        return result
    }

    override fun toString(): String {
        return "SkelligTable"
    }

    companion object {
        private val HEADER_ROW_TOKEN_SET = TokenSet.create(SkelligElementTypes.Companion.TABLE_HEADER_ROW)
    }
}