// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber

import com.intellij.psi.PsiElement

interface ParameterTypeManager {
    /**
     * @return value of Parameter Type with name
     */
    fun getParameterTypeValue(name: String): String?

    /**
     * @return element (String Literal) that declares Parameter Type with name
     */
    fun getParameterTypeDeclaration(name: String): PsiElement?
}