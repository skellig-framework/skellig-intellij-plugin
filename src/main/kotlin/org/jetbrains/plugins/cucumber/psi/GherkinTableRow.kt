package org.jetbrains.plugins.cucumber.psi

interface GherkinTableRow : GherkinPsiElement {
    val psiCells: List<GherkinTableCell>
    fun getColumnWidth(columnIndex: Int): Int
    fun deleteCell(columnIndex: Int)

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinTableRow>(0)
    }
}