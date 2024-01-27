package org.skellig.plugin.language.feature.steps.reference.kotlin

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.feature.steps.reference.SkelligStepReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToFeatureReference

class KotlinToSkelligTestStepReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(KtStringTemplateExpression::class.java),
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
                        PsiTreeUtil.getParentOfType(element, KtAnnotationEntry::class.java)?.let {
                            if (it.getChildOfType<KtConstructorCalleeExpression>()?.text == "TestStep") {
                                val testStepDef = SkelligUtil.getTestStepName(it)
                                if (testStepDef.isNotEmpty()) {
                                    val textRange = TextRange(1, testStepDef.length + 1)
                                    return arrayOf(SkelligTestStepToFeatureReference(element, textRange))
                                }
                            }
                        }
                    }
                    return PsiReference.EMPTY_ARRAY
                }
            })
    }

    private fun getTestStepName(element: PsiElement): String? {
        val literalExpression = element as KtStringTemplateExpression
        val value = literalExpression.getChildOfType<KtLiteralStringTemplateEntry>()?.text
        return if (value?.isNotEmpty() == true) {
            PsiTreeUtil.getParentOfType(element, KtCallExpression::class.java)?.let {
                if (it.getChildOfType<KtNameReferenceExpression>()?.text == "run")
                    value
                else null
            }
        } else null
    }

}