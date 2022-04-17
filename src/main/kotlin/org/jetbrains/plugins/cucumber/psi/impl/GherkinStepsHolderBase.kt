package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.cucumber.psi.GherkinStep
import org.jetbrains.plugins.cucumber.psi.GherkinStepsHolder
import org.jetbrains.plugins.cucumber.psi.GherkinTag
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes

abstract class GherkinStepsHolderBase protected constructor(node: ASTNode) : GherkinPsiElementBase(node), GherkinStepsHolder {
    override val scenarioName: String
        get() {
            val result = StringBuilder()
            var node = node.firstChildNode
            while (node != null && node.elementType !== GherkinTokenTypes.Companion.COLON) {
                node = node.treeNext
            }
            if (node != null) {
                node = node.treeNext
            }
            while (node != null && !node.text.contains("\n")) {
                result.append(node.text)
                node = node.treeNext
            }
            return result.toString().trim { it <= ' ' }
        }
    override val steps: Array<GherkinStep?>
        get() {
            val steps = PsiTreeUtil.getChildrenOfType(this, GherkinStep::class.java)
            return steps ?: GherkinStep.Companion.EMPTY_ARRAY
        }
    override val tags: Array<GherkinTag?>
        get() {
            val tags = PsiTreeUtil.getChildrenOfType(this, GherkinTag::class.java)
            return tags ?: GherkinTag.Companion.EMPTY_ARRAY
        }
    override val scenarioKeyword: String
        get() = firstChild.text
}