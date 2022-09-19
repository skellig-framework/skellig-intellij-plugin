package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor

class GherkinFeatureHeaderImpl(node: ASTNode) : GherkinPsiElementBase(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitFeatureHeader(this)
    }

    override fun toString(): String {
        return "GherkinFeatureHeader"
    }
}