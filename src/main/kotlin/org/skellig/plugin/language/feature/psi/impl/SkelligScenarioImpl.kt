package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligScenario
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes

class SkelligScenarioImpl(node: ASTNode) : SkelligStepsHolderBase(node), SkelligScenario {
    override fun toString(): String {
        return if (isBackground) {
            "SkelligScenario(Background):"
        } else "SkelligScenario:$scenarioName"
    }

    override val isBackground: Boolean
        get() {
            val node = node.firstChildNode
            return node != null && node.elementType === SkelligTokenTypes.Companion.BACKGROUND_KEYWORD
        }
    override fun getPresentableText(): String {
        return buildPresentableText(if (isBackground) "Background" else scenarioKeyword)
    }

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitScenario(this)
    }
}