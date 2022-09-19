package org.skellig.plugin.language.feature.psi

interface GherkinScenarioOutline : GherkinStepsHolder {
    fun getExamplesBlocks(): List<GherkinExamplesBlock>
    fun getOutlineTableMap(): Map<String, String>?
}