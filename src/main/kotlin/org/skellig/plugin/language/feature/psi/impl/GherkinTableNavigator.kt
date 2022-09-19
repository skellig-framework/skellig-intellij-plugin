package org.skellig.plugin.language.feature.psi.impl

import org.skellig.plugin.language.feature.psi.GherkinTableRow

object GherkinTableNavigator {
    fun getTableByRow(row: GherkinTableRow): GherkinTableImpl? {
        val element = row.parent
        return if (element is GherkinTableImpl) element else null
    }
}