package org.jetbrains.plugins.cucumber.psi

interface GherkinRule : GherkinPsiElement, GherkinSuppressionHolder {
    val ruleName: String
    val scenarios: Array<GherkinStepsHolder?>
}