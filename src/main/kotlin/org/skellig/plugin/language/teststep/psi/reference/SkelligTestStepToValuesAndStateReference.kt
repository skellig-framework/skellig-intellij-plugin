package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang.StringUtils
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepReferenceKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import java.util.regex.Pattern


class SkelligTestStepToValuesAndStateReference(stepParameter: SkelligTestStepReferenceKey) : SkelligTestStepSimpleReference(stepParameter) {
    val element: SkelligTestStepReferenceKey
        get() = super.getElement() as SkelligTestStepReferenceKey

    override fun resolve(): PsiElement? {
        val parameterText = element.text
        if (StringUtils.isNumeric(parameterText)) {
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStepTestStepNameExpression::class.java) ?: return null
            val groupPattern = Pattern.compile("\\(.*\\)")
            val matcher = groupPattern.matcher(testStep.testStepName.text)
            if (matcher.find()) {
                return testStep
            }
        } else {
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStepTestStepNameExpression::class.java) ?: return null
            val pairs = PsiTreeUtil.getChildrenOfTypeAsList(testStep, SkelligTestStepPair::class.java)
                .find { it.key.text == "values" }
            pairs?.let { values ->
                values.map?.let {
                    for (pair in it.pairList) {
                        val text = pair.key.text
                        if (text.startsWith(parameterText)) {
                            return pair
                        }
                    }
                }
            }
        }
        return null
    }
}

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

    override fun handleElementRename(newElementName: String): PsiElement {
        if (myElement is PsiNamedElement) {
            myElement.setName(newElementName)
        }
        return myElement
    }

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