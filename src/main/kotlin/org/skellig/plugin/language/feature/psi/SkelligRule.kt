package org.skellig.plugin.language.feature.psi

interface SkelligRule : SkelligPsiElement, SkelligSuppressionHolder {
    val ruleName: String
    val scenarios: Array<SkelligStepsHolder?>
}