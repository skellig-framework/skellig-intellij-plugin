package org.jetbrains.plugins.cucumber.psi

interface GherkinExamplesBlock : GherkinPsiElement {
    fun getTable(): GherkinTable?
}