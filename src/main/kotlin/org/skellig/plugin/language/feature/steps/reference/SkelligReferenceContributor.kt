package org.skellig.plugin.language.feature.steps.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep

class SkelligReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(SkelligFeatureStep::class.java),
            SkelligStepReferenceProvider(),
        )
    }
}
