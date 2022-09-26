package org.skellig.plugin.language.feature.psi.impl

object SkelligExamplesNavigator {
    fun getExamplesByTable(table: SkelligTableImpl): SkelligExamplesBlockImpl? {
        val element = table.parent
        return if (element is SkelligExamplesBlockImpl) element else null
    }
}