package org.skellig.plugin.language.feature.psi

interface SkelligFeature : SkelligPsiElement, SkelligSuppressionHolder {
    val featureName: String
    val scenarios: Array<SkelligStepsHolder>?
}