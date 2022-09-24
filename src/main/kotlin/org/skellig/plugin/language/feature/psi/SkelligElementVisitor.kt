package org.skellig.plugin.language.feature.psi

import com.intellij.psi.PsiElementVisitor
import org.skellig.plugin.language.feature.psi.impl.*

abstract class SkelligElementVisitor : PsiElementVisitor() {
    fun visitFeature(feature: SkelligFeature?) {
        visitElement(feature)
    }

    fun visitRule(rule: SkelligRule?) {
        visitElement(rule)
    }

    fun visitFeatureHeader(header: SkelligFeatureHeaderImpl?) {
        visitElement(header)
    }

    fun visitScenario(scenario: SkelligScenario?) {
        visitElement(scenario)
    }

    fun visitScenarioOutline(outline: SkelligScenarioOutline?) {
        visitElement(outline)
    }

    fun visitExamplesBlock(block: SkelligExamplesBlockImpl?) {
        visitElement(block)
    }

    fun visitStep(step: SkelligFeatureStep?) {
        visitElement(step)
    }

    fun visitTable(table: SkelligTableImpl?) {
        visitElement(table)
    }

    fun visitTableRow(row: SkelligTableRowImpl?) {
        visitElement(row)
    }

    fun visitTableHeaderRow(row: SkelligTableHeaderRowImpl?) {
        visitElement(row)
    }

    fun visitTag(skelligTag: SkelligTagImpl?) {
        visitElement(skelligTag)
    }

    fun visitStepParameter(skelligStepParameter: SkelligStepParameterImpl?) {
        visitElement(skelligStepParameter)
    }

    fun visitSkelligTableCell(cell: SkelligTableCell?) {
        visitElement(cell)
    }

    fun visitPystring(phstring: SkelligPystring?) {
        visitElement(phstring)
    }
}