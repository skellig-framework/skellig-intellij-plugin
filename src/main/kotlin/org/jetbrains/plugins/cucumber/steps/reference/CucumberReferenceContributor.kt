package org.jetbrains.plugins.cucumber.steps.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.patterns.PlatformPatterns
import org.jetbrains.plugins.cucumber.psi.impl.GherkinStepImpl
import org.jetbrains.plugins.cucumber.psi.impl.GherkinStepParameterImpl
import org.jetbrains.plugins.cucumber.steps.reference.CucumberStepReferenceProvider

class CucumberReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(GherkinStepImpl::class.java),
            CucumberStepReferenceProvider()
        )
    }
}