package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligFeature
import org.skellig.plugin.language.feature.psi.SkelligStepsHolder
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes
import java.util.*

class SkelligFeatureImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligFeature {
    override fun toString(): String {
        return "SkelligFeature:$featureName"
    }

    override val featureName: String
        get() {
            val node = node
            val firstText: ASTNode? = node.findChildByType(SkelligTokenTypes.Companion.TEXT)
            if (firstText != null) {
                return firstText.text
            }
            val header = PsiTreeUtil.getChildOfType(this, SkelligFeatureHeaderImpl::class.java)
            return header?.elementText ?: elementText
        }
    override val scenarios: Array<SkelligStepsHolder>?
        get() {
            val result: MutableList<SkelligStepsHolder> = ArrayList()
            var scenarios = PsiTreeUtil.getChildrenOfType(this, SkelligStepsHolder::class.java)
            if (scenarios != null) {
                result.addAll(Arrays.asList(*scenarios))
            }
            val rules = PsiTreeUtil.getChildrenOfType(this, SkelligRuleImpl::class.java)
            if (rules != null) {
                for (rule in rules) {
                    scenarios = PsiTreeUtil.getChildrenOfType(rule, SkelligStepsHolder::class.java)
                    if (scenarios != null) {
                        result.addAll(Arrays.asList(*scenarios))
                    }
                }
            }
            return result.toTypedArray()
        }

    protected override fun getPresentableText(): String? {
        return "Feature: $featureName"
    }

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitFeature(this)
    }
}