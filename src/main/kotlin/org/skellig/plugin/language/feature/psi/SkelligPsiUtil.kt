package org.skellig.plugin.language.feature.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.feature.OutlineStepSubstitution
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition

object SkelligPsiUtil {

    fun getSkelligFile(element: PsiElement): SkelligFile? {
        if (!element.isValid) {
            return null
        }
        val containingFile = element.containingFile
        return if (containingFile is SkelligFile) containingFile else null
    }

    fun buildParameterRanges(
        step: SkelligFeatureStep,
        definition: AbstractStepDefinition,
        shiftOffset: Int
    ): List<TextRange>? {
        val substitution = convertOutlineStepName(step)
        val parameterRanges: MutableList<TextRange> = ArrayList()
        val pattern = definition.getPattern() ?: return null
        val matcher = pattern.matcher(substitution.substitution)
        if (matcher.find()) {
            val groupCount = matcher.groupCount()
            for (i in 0 until groupCount) {
                val start = matcher.start(i + 1)
                val end = matcher.end(i + 1)
                if (start >= 0 && end >= 0) {
                    val rangeStart = substitution.getOffsetInOutlineStep(start)
                    val rangeEnd = substitution.getOffsetInOutlineStep(end)
                    val range = TextRange(rangeStart, rangeEnd).shiftRight(shiftOffset)
                    if (!parameterRanges.contains(range)) {
                        parameterRanges.add(range)
                    }
                }
            }
        }
        var k = step.text.indexOf(step.name)
        k += step.name.length
        if (k < step.text.length - 1) {
            val text = step.text.substring(k + 1)
            var inParam = false
            var paramStart = 0
            var i = 0
            while (i < text.length) {
                if (text[i] == '<') {
                    paramStart = i
                    inParam = true
                }
                if (text[i] == '>' && inParam) {
                    parameterRanges.add(TextRange(paramStart, i + 1).shiftRight(shiftOffset + step.name.length + 1))
                    inParam = false
                }
                i++
            }
        }
        return parameterRanges
    }

    fun convertOutlineStepName(step: SkelligFeatureStep): OutlineStepSubstitution {
        if (step.stepHolder !is SkelligScenarioOutline) {
            return OutlineStepSubstitution(step.name)
        }
        val outlineTableMap = (step.stepHolder as SkelligScenarioOutline).getOutlineTableMap()
        return org.skellig.plugin.language.feature.SkelligUtil.substituteTableReferences(step.name, outlineTableMap)
    }
}