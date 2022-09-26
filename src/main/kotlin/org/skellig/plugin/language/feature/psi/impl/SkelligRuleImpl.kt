package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligRule
import org.skellig.plugin.language.feature.psi.SkelligStepsHolder
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes

class SkelligRuleImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligRule {
    override fun toString(): String {
        return "SkelligRule:$ruleName"
    }

    override val ruleName: String
        get() {
            val node = node
            val firstText: ASTNode? = node.findChildByType(SkelligTokenTypes.Companion.TEXT)
            return firstText?.text ?: elementText
        }
    override val scenarios: Array<SkelligStepsHolder?>
        get() {
            val children = PsiTreeUtil.getChildrenOfType(this, SkelligStepsHolder::class.java)
            return children ?: SkelligStepsHolder.Companion.EMPTY_ARRAY
        }
    override fun getPresentableText(): String = "Rule: $ruleName"

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitRule(this)
    }
}