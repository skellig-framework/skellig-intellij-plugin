package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor

class SkelligFeatureHeaderImpl(node: ASTNode) : SkelligPsiElementBase(node) {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitFeatureHeader(this)
    }

    override fun toString(): String {
        return "SkelligFeatureHeader"
    }
}