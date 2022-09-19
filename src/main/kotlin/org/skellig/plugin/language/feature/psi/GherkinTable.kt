package org.skellig.plugin.language.feature.psi

interface GherkinTable : GherkinPsiElement {
    val headerRow: GherkinTableRow?
    val dataRows: List<GherkinTableRow?>
    fun getColumnWidth(columnIndex: Int): Int
}