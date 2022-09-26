package org.skellig.plugin.language.feature.psi

interface SkelligTag : SkelligPsiElement {
    val tagName: String?

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<SkelligTag>(0)
    }
}