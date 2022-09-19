package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinRule
import org.skellig.plugin.language.feature.psi.GherkinStepsHolder
import org.skellig.plugin.language.feature.psi.GherkinTokenTypes

class GherkinRuleImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinRule {
    override fun toString(): String {
        return "GherkinRule:$ruleName"
    }

    override val ruleName: String
        get() {
            val node = node
            val firstText: ASTNode? = node.findChildByType(GherkinTokenTypes.Companion.TEXT)
            return firstText?.text ?: elementText
        }
    override val scenarios: Array<GherkinStepsHolder?>
        get() {
            val children = PsiTreeUtil.getChildrenOfType(this, GherkinStepsHolder::class.java)
            return children ?: GherkinStepsHolder.Companion.EMPTY_ARRAY
        }
    override fun getPresentableText(): String = "Rule: $ruleName"

    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitRule(this)
    }
}