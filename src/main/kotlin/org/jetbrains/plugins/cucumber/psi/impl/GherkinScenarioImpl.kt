package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor
import org.jetbrains.plugins.cucumber.psi.GherkinScenario
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes

class GherkinScenarioImpl(node: ASTNode) : GherkinStepsHolderBase(node), GherkinScenario {
    override fun toString(): String {
        return if (isBackground) {
            "GherkinScenario(Background):"
        } else "GherkinScenario:$scenarioName"
    }

    override val isBackground: Boolean
        get() {
            val node = node.firstChildNode
            return node != null && node.elementType === GherkinTokenTypes.Companion.BACKGROUND_KEYWORD
        }
    override fun getPresentableText(): String {
        return buildPresentableText(if (isBackground) "Background" else scenarioKeyword)
    }

    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitScenario(this)
    }
}