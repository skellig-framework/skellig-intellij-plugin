package org.jetbrains.plugins.cucumber.psi.impl

import org.jetbrains.plugins.cucumber.psi.GherkinTableRow

object GherkinTableNavigator {
    fun getTableByRow(row: GherkinTableRow): GherkinTableImpl? {
        val element = row.parent
        return if (element is GherkinTableImpl) element else null
    }
}