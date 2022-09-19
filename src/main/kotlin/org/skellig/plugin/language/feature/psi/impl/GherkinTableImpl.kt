package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.GherkinElementTypes
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinTable
import org.skellig.plugin.language.feature.psi.GherkinTableRow

class GherkinTableImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinTable {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitTable(this)
    }

    override val headerRow: GherkinTableRow?
        get() {
            val node = node
            val tableNode = node.findChildByType(HEADER_ROW_TOKEN_SET)
            return if (tableNode == null) null else tableNode.psi as GherkinTableRow
        }
    override val dataRows: List<GherkinTableRow?>
        get() {
            val result: MutableList<GherkinTableRow?> = ArrayList()
            val rows = PsiTreeUtil.getChildrenOfType(this, GherkinTableRow::class.java)
            if (rows != null) {
                for (row in rows) {
                    if (row !is GherkinTableHeaderRowImpl) {
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
        return "GherkinTable"
    }

    companion object {
        private val HEADER_ROW_TOKEN_SET = TokenSet.create(GherkinElementTypes.Companion.TABLE_HEADER_ROW)
    }
}