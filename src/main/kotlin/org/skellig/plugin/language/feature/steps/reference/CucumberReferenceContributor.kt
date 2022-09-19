package org.skellig.plugin.language.feature.steps.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.patterns.PlatformPatterns
import org.skellig.plugin.language.feature.psi.impl.GherkinStepImpl

class CucumberReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(GherkinStepImpl::class.java),
            CucumberStepReferenceProvider()
        )
    }
}