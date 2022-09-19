package org.skellig.plugin.language.feature.psi.impl

object GherkinExamplesNavigator {
    fun getExamplesByTable(table: GherkinTableImpl): GherkinExamplesBlockImpl? {
        val element = table.parent
        return if (element is GherkinExamplesBlockImpl) element else null
    }
}