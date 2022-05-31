package org.jetbrains.plugins.cucumber.psi

interface GherkinFeature : GherkinPsiElement, GherkinSuppressionHolder {
    val featureName: String
    val scenarios: Array<GherkinStepsHolder>?
}