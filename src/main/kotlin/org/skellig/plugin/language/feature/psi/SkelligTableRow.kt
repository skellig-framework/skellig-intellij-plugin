package org.skellig.plugin.language.feature.psi

interface SkelligTableRow : SkelligPsiElement {
    val psiCells: List<SkelligTableCell>
    fun getColumnWidth(columnIndex: Int): Int
    fun deleteCell(columnIndex: Int)

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<SkelligTableRow>(0)
    }
}