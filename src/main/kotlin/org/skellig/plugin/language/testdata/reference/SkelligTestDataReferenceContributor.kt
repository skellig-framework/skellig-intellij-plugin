package org.skellig.plugin.language.testdata.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.psi.SkelligFeatureTextOrString


class SkelligTestDataReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(SkelligFeatureTextOrString::class.java),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement,
                                                        context: ProcessingContext): Array<PsiReference> {
                        val literalExpression: SkelligFeatureStep = element as SkelligFeatureStep
                        val value: String? = literalExpression.value as String
                        /*if (value != null && value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR)) {
                            val property = TextRange(SIMPLE_PREFIX_STR.length() + SIMPLE_SEPARATOR_STR.length() + 1,
                                    value.length + 1)
                            return arrayOf(SimpleReference(element, property))
                        }*/
                        return arrayOf(SkelligTestDataReference(element, TextRange(1,5)))
                    }
                })
    }

}