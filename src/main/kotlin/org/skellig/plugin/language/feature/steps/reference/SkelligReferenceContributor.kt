package org.skellig.plugin.language.feature.steps.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.patterns.PlatformPatterns
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.psi.impl.SkelligFeatureStepImpl

class SkelligReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(SkelligFeatureStep::class.java),
            SkelligStepReferenceProvider()
        )
    }
}