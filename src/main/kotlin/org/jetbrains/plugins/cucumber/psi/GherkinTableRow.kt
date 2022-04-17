// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

interface GherkinTableRow : GherkinPsiElement {
    val psiCells: List<GherkinTableCell>
    fun getColumnWidth(columnIndex: Int): Int
    fun deleteCell(columnIndex: Int)

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinTableRow>(0)
    }
}