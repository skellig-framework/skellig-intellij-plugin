package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor

class GherkinTableHeaderRowImpl(node: ASTNode) : GherkinTableRowImpl(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitTableHeaderRow(this)
    }

    override fun toString(): String {
        return "GherkinTableHeaderRow"
    }
}