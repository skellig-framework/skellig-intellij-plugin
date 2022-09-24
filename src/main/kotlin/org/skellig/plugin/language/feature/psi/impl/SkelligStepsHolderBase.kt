package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.psi.SkelligStepsHolder
import org.skellig.plugin.language.feature.psi.SkelligTag
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes

abstract class SkelligStepsHolderBase protected constructor(node: ASTNode) : SkelligPsiElementBase(node), SkelligStepsHolder {
    override val scenarioName: String
        get() {
            val result = StringBuilder()
            var node = node.firstChildNode
            while (node != null && node.elementType !== SkelligTokenTypes.Companion.COLON) {
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
    override val steps: Array<SkelligFeatureStep?>
        get() {
            val steps = PsiTreeUtil.getChildrenOfType(this, SkelligFeatureStep::class.java)
            return steps ?: SkelligFeatureStep.Companion.EMPTY_ARRAY
        }
    override val tags: Array<SkelligTag?>
        get() {
            val tags = PsiTreeUtil.getChildrenOfType(this, SkelligTag::class.java)
            return tags ?: SkelligTag.Companion.EMPTY_ARRAY
        }
    override val scenarioKeyword: String
        get() = firstChild.text
}