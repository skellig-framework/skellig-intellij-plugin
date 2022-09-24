package org.skellig.plugin.language.feature.psi

interface SkelligExamplesBlock : SkelligPsiElement {
    fun getTable(): SkelligTable?
}