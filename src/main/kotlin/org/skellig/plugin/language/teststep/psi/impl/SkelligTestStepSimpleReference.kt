package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.util.IncorrectOperationException

open class SkelligTestStepSimpleReference(private val myElement: PsiElement) : PsiReference {
    override fun getElement(): PsiElement {
        return myElement
    }

    override fun getRangeInElement(): TextRange {
        return TextRange(0, myElement.textLength)
    }

    override fun resolve(): PsiElement? {
        return myElement
    }

    override fun getCanonicalText(): String {
        return myElement.text
    }

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        if (myElement is PsiNamedElement) {
            (myElement as PsiNamedElement).setName(newElementName)
        }
        return myElement
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(element: PsiElement): PsiElement {
        return myElement
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val myResolved = resolve()
        var resolved = if (element.reference != null) element.reference!!.resolve() else null
        if (resolved == null) {
            resolved = element
        }
        return myResolved != null && resolved == myResolved
    }

    override fun isSoft(): Boolean {
        return false
    }
}