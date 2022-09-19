package org.skellig.plugin.language.feature.psi

interface GherkinFeature : GherkinPsiElement, GherkinSuppressionHolder {
    val featureName: String
    val scenarios: Array<GherkinStepsHolder>?
}