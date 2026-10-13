package org.skellig.plugin.language.feature.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.util.IncorrectOperationException
import org.skellig.plugin.language.feature.psi.SkelligPsiElement

open class SkelligSimpleReference(
    private val myElement: SkelligPsiElement,
) : PsiReference {
    override fun getElement(): PsiElement = myElement

    override fun getRangeInElement(): TextRange = TextRange(0, myElement.textLength)

    override fun resolve(): PsiElement? = myElement

    override fun getCanonicalText(): String = myElement.text

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        if (myElement is PsiNamedElement) {
            (myElement as PsiNamedElement).setName(newElementName)
        }
        return myElement
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(element: PsiElement): PsiElement = myElement

    override fun isReferenceTo(element: PsiElement): Boolean {
        val myResolved = resolve()
        var resolved = if (element.reference != null) element.reference!!.resolve() else null
        if (resolved == null) {
            resolved = element
        }
        return myResolved != null && resolved == myResolved
    }

    override fun isSoft(): Boolean = false
}
