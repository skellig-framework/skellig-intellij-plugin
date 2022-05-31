package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor

class GherkinFeatureHeaderImpl(node: ASTNode) : GherkinPsiElementBase(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitFeatureHeader(this)
    }

    override fun toString(): String {
        return "GherkinFeatureHeader"
    }
}