package org.jetbrains.plugins.cucumber.psi

interface GherkinScenarioOutline : GherkinStepsHolder {
    fun getExamplesBlocks(): List<GherkinExamplesBlock>
    fun getOutlineTableMap(): Map<String, String>?
}