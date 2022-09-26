package org.skellig.plugin.language.feature

import com.intellij.psi.PsiElement

interface ParameterTypeManager {

    fun getParameterTypeValue(name: String): String?

    fun getParameterTypeDeclaration(name: String): PsiElement?
}