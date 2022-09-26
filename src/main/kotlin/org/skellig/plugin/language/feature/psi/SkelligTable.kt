package org.skellig.plugin.language.feature.psi

interface SkelligTable : SkelligPsiElement {
    val headerRow: SkelligTableRow?
    val dataRows: List<SkelligTableRow?>
    fun getColumnWidth(columnIndex: Int): Int
}