package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import org.skellig.plugin.language.teststep.SkelligTestStepPsiImplUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression

class SkelligTestStepRefReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        if (element is SkelligTestStepFunctionExpression) {
            val reference = SkelligTestStepPsiImplUtil.getReference(element)
            if (reference != null) return arrayOf(reference)
        }
        return PsiReference.EMPTY_ARRAY
    }
}
