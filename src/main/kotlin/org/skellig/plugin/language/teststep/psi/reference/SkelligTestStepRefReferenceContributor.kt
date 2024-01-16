package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestNameKeyword
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.impl.SkelligTestStepFunctionNameImpl

class SkelligTestStepRefReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(SkelligTestStepFunctionNameImpl::class.java),
            SkelligTestStepRefReferenceProvider()
        )
    }
}