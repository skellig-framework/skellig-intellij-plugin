package org.jetbrains.plugins.cucumber.psi

interface GherkinTable : GherkinPsiElement {
    val headerRow: GherkinTableRow?
    val dataRows: List<GherkinTableRow?>
    fun getColumnWidth(columnIndex: Int): Int
}