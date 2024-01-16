package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName

class SkelligTestStepRefReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element is SkelligTestStepFunctionExpression) {
            if (element.functionName.text == "get")
                return arrayOf(SkelligTestStepRefReference(element))
        }
        return PsiReference.EMPTY_ARRAY
    }
}