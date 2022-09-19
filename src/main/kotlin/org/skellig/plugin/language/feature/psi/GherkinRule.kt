package org.skellig.plugin.language.feature.psi

interface GherkinRule : GherkinPsiElement, GherkinSuppressionHolder {
    val ruleName: String
    val scenarios: Array<GherkinStepsHolder?>
}