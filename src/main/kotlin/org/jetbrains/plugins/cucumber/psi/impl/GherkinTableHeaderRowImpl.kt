package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor

class GherkinTableHeaderRowImpl(node: ASTNode) : GherkinTableRowImpl(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitTableHeaderRow(this)
    }

    override fun toString(): String {
        return "GherkinTableHeaderRow"
    }
}