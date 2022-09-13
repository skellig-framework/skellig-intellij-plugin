package org.jetbrains.plugins.cucumber.psi.impl

import org.jetbrains.plugins.cucumber.psi.GherkinStepParameter
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.cucumber.psi.GherkinScenarioOutline
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.cucumber.psi.GherkinExamplesBlock
import org.jetbrains.plugins.cucumber.psi.GherkinTable
import org.jetbrains.plugins.cucumber.psi.impl.GherkinTableHeaderRowImpl
import org.jetbrains.plugins.cucumber.psi.GherkinTableCell

class GherkinStepParameterReference(stepParameter: GherkinStepParameter) : GherkinSimpleReference(stepParameter) {
    val element: GherkinStepParameter
        get() = super.getElement() as GherkinStepParameter

    override fun resolve(): PsiElement? {
        val scenario = PsiTreeUtil.getParentOfType(element, GherkinScenarioOutline::class.java) ?: return null
        val exampleBlock = PsiTreeUtil.getChildOfType(scenario, GherkinExamplesBlock::class.java) ?: return null
        val table = PsiTreeUtil.getChildOfType(exampleBlock, GherkinTable::class.java) ?: return null
        val header = PsiTreeUtil.getChildOfType(table, GherkinTableHeaderRowImpl::class.java) ?: return null
        for (cell in header.children) {
            if (cell is GherkinTableCell) {
                val cellText = cell.getText()
                if (cellText == element.name) {
                    return cell
                }
            }
        }
        return null
    }
}