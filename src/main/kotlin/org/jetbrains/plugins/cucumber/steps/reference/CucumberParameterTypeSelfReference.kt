package org.jetbrains.plugins.cucumber.steps.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange

class CucumberParameterTypeSelfReference(element: PsiElement) : PsiReferenceBase<PsiElement?>(element, TextRange.create(1, element.textLength - 1)) {
    override fun resolve(): PsiElement? {
        return element
    }
}