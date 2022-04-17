package org.jetbrains.plugins.cucumber.psi

/**
 * @author Roman.Chernyatchik
 * @date Aug 22, 2009
 */
interface GherkinStepsHolder : GherkinPsiElement, GherkinSuppressionHolder {
    val scenarioName: String
    val steps: Array<GherkinStep?>
    val tags: Array<GherkinTag?>
    val scenarioKeyword: String

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<GherkinStepsHolder>(0)
    }
}