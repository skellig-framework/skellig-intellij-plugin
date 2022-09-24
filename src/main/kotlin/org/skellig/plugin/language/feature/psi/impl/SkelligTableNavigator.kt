package org.skellig.plugin.language.feature.psi.impl

import org.skellig.plugin.language.feature.psi.SkelligTableRow

object SkelligTableNavigator {
    fun getTableByRow(row: SkelligTableRow): SkelligTableImpl? {
        val element = row.parent
        return if (element is SkelligTableImpl) element else null
    }
}