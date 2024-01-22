package org.skellig.plugin.language.feature.steps.reference.java

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.feature.steps.reference.SkelligStepReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToFeatureReference

class JavaToSkelligTestStepReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                    val testStepName = getTestStepName(element)
                    if (!testStepName.isNullOrEmpty()) {
                        val textRange = TextRange(1, testStepName.length + 1)
                        return arrayOf(SkelligStepReference(element, textRange))
                    } else {
                        PsiTreeUtil.getParentOfType(element, PsiAnnotation::class.java)?.let {
                            if(it.nameReferenceElement?.text == "TestStep") {
                                val testStepDef = SkelligUtil.getTestStepName(it)
                                if (testStepDef.isNotEmpty()) {
                                    return arrayOf(SkelligTestStepToFeatureReference(it))
                                }
                            }
                        }
                    }
                    return PsiReference.EMPTY_ARRAY
                }
            })
    }

    private fun getTestStepName(element: PsiElement): String? {
        val literalExpression = element as PsiLiteralExpression
        val value = literalExpression.value
        return if (value is String && value.isNotEmpty()) {
            PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression::class.java)?.let {
                if (it.children.isNotEmpty() && it.children[0].getChildOfType<PsiIdentifier>()?.text == "run")
                    value
                else null
            }
        } else null
    }

}