package org.skellig.plugin.language.feature.psi

interface SkelligScenarioOutline : SkelligStepsHolder {
    fun getExamplesBlocks(): List<SkelligExamplesBlock>
    fun getOutlineTableMap(): Map<String, String>?
}