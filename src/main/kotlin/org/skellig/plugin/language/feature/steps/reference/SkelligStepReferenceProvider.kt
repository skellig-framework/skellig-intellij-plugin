package org.skellig.plugin.language.feature.steps.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.util.ProcessingContext
import org.skellig.plugin.language.feature.psi.SkelligElementTypes.Companion.STEP_PARAMETER
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes.Companion.STEP_PARAMETER_BRACE
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes.Companion.STEP_PARAMETER_TEXT
import org.skellig.plugin.language.feature.psi.SkelligTokenTypes.Companion.TEXT
import org.skellig.plugin.language.feature.psi.impl.SkelligFeatureStepImpl

class SkelligStepReferenceProvider : PsiReferenceProvider() {
    companion object {
        private val TEXT_AND_PARAM_SET = TokenSet.create(TEXT, STEP_PARAMETER_TEXT, STEP_PARAMETER_BRACE, STEP_PARAMETER)
        private val TEXT_PARAM_AND_WHITE_SPACE_SET = TokenSet.orSet(TEXT_AND_PARAM_SET, TokenSet.WHITE_SPACE)
    }

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element is SkelligFeatureStepImpl) {
            var textNode = element.node.findChildByType(TEXT_AND_PARAM_SET)
            if (textNode != null && !element.text.contains("Idea")) {
                val start = textNode.textRange.startOffset
                var end = textNode.textRange.endOffset
                var endBeforeSpace = end
                textNode = textNode.treeNext
                while (textNode != null && TEXT_PARAM_AND_WHITE_SPACE_SET.contains(textNode.elementType)) {
                    endBeforeSpace = if (textNode.elementType === TokenType.WHITE_SPACE) end else textNode.textRange.endOffset
                    end = textNode.textRange.endOffset
                    textNode = textNode.treeNext
                }
                val textRange = TextRange(start, endBeforeSpace)
                val reference = SkelligStepReference(element, textRange.shiftRight(-element.getTextOffset()))
                return arrayOf(reference)
            }
        }
        return PsiReference.EMPTY_ARRAY
    }
}