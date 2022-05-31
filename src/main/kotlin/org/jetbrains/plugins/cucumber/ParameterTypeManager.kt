package org.jetbrains.plugins.cucumber

import com.intellij.psi.PsiElement

interface ParameterTypeManager {

    fun getParameterTypeValue(name: String): String?

    fun getParameterTypeDeclaration(name: String): PsiElement?
}