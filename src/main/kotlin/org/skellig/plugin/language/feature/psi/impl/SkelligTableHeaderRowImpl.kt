package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor

class SkelligTableHeaderRowImpl(node: ASTNode) : SkelligTableRowImpl(node) {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitTableHeaderRow(this)
    }

    override fun toString(): String {
        return "SkelligTableHeaderRow"
    }
}