package org.jetbrains.plugins.cucumber.psi

interface GherkinStepsHolder : GherkinPsiElement, GherkinSuppressionHolder {
    val scenarioName: String
    val steps: Array<GherkinStep?>
    val tags: Array<GherkinTag?>
    val scenarioKeyword: String

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinStepsHolder>(0)
    }
}