package org.skellig.plugin.language.feature.psi.impl

import org.skellig.plugin.language.feature.psi.SkelligStepParameter
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.feature.psi.SkelligScenarioOutline
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.SkelligExamplesBlock
import org.skellig.plugin.language.feature.psi.SkelligTable
import org.skellig.plugin.language.feature.psi.SkelligTableCell

class SkelligStepParameterReference(stepParameter: SkelligStepParameter) : SkelligSimpleReference(stepParameter) {
    val element: SkelligStepParameter
        get() = super.getElement() as SkelligStepParameter

    override fun resolve(): PsiElement? {
        val scenario = PsiTreeUtil.getParentOfType(element, SkelligScenarioOutline::class.java) ?: return null
        val exampleBlock = PsiTreeUtil.getChildOfType(scenario, SkelligExamplesBlock::class.java) ?: return null
        val table = PsiTreeUtil.getChildOfType(exampleBlock, SkelligTable::class.java) ?: return null
        val header = PsiTreeUtil.getChildOfType(table, SkelligTableHeaderRowImpl::class.java) ?: return null
        for (cell in header.children) {
            if (cell is SkelligTableCell) {
                val cellText = cell.getText()
                if (cellText == element.name) {
                    return cell
                }
            }
        }
        return null
    }
}