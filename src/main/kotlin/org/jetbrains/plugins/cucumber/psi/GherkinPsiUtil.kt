package org.jetbrains.plugins.cucumber.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.cucumber.CucumberUtil
import org.jetbrains.plugins.cucumber.OutlineStepSubstitution
import org.jetbrains.plugins.cucumber.psi.impl.GherkinFileImpl
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition

object GherkinPsiUtil {

    fun getGherkinFile(element: PsiElement): GherkinFile? {
        if (!element.isValid) {
            return null
        }
        val containingFile = element.containingFile
        return if (containingFile is GherkinFile) containingFile else null
    }

    fun buildParameterRanges(
        step: GherkinStep,
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

    fun convertOutlineStepName(step: GherkinStep): OutlineStepSubstitution {
        if (step.stepHolder !is GherkinScenarioOutline) {
            return OutlineStepSubstitution(step.name)
        }
        val outlineTableMap = (step.stepHolder as GherkinScenarioOutline).getOutlineTableMap()
        return CucumberUtil.substituteTableReferences(step.name, outlineTableMap)
    }
}