package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinFeature
import org.skellig.plugin.language.feature.psi.GherkinStepsHolder
import org.skellig.plugin.language.feature.psi.GherkinTokenTypes
import java.util.*

class GherkinFeatureImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinFeature {
    override fun toString(): String {
        return "GherkinFeature:$featureName"
    }

    override val featureName: String
        get() {
            val node = node
            val firstText: ASTNode? = node.findChildByType(GherkinTokenTypes.Companion.TEXT)
            if (firstText != null) {
                return firstText.text
            }
            val header = PsiTreeUtil.getChildOfType(this, GherkinFeatureHeaderImpl::class.java)
            return header?.elementText ?: elementText
        }
    override val scenarios: Array<GherkinStepsHolder>?
        get() {
            val result: MutableList<GherkinStepsHolder> = ArrayList()
            var scenarios = PsiTreeUtil.getChildrenOfType(this, GherkinStepsHolder::class.java)
            if (scenarios != null) {
                result.addAll(Arrays.asList(*scenarios))
            }
            val rules = PsiTreeUtil.getChildrenOfType(this, GherkinRuleImpl::class.java)
            if (rules != null) {
                for (rule in rules) {
                    scenarios = PsiTreeUtil.getChildrenOfType(rule, GherkinStepsHolder::class.java)
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

    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitFeature(this)
    }
}