package org.skellig.plugin.language.feature.psi

interface GherkinExamplesBlock : GherkinPsiElement {
    fun getTable(): GherkinTable?
}