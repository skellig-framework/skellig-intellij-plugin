package org.skellig.plugin.language.feature.psi

interface SkelligStepsHolder : SkelligPsiElement, SkelligSuppressionHolder {
    val scenarioName: String
    val steps: Array<SkelligFeatureStep?>
    val tags: Array<SkelligTag?>
    val scenarioKeyword: String

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<SkelligStepsHolder>(0)
    }
}