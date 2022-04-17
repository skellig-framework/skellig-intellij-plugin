package org.jetbrains.plugins.cucumber.psi

import com.intellij.psi.PsiElementVisitor
import org.jetbrains.plugins.cucumber.psi.impl.*

abstract class GherkinElementVisitor : PsiElementVisitor() {
    fun visitFeature(feature: GherkinFeature?) {
        visitElement(feature)
    }

    fun visitRule(rule: GherkinRule?) {
        visitElement(rule)
    }

    fun visitFeatureHeader(header: GherkinFeatureHeaderImpl?) {
        visitElement(header)
    }

    fun visitScenario(scenario: GherkinScenario?) {
        visitElement(scenario)
    }

    fun visitScenarioOutline(outline: GherkinScenarioOutline?) {
        visitElement(outline)
    }

    fun visitExamplesBlock(block: GherkinExamplesBlockImpl?) {
        visitElement(block)
    }

    fun visitStep(step: GherkinStep?) {
        visitElement(step)
    }

    fun visitTable(table: GherkinTableImpl?) {
        visitElement(table)
    }

    fun visitTableRow(row: GherkinTableRowImpl?) {
        visitElement(row)
    }

    fun visitTableHeaderRow(row: GherkinTableHeaderRowImpl?) {
        visitElement(row)
    }

    fun visitTag(gherkinTag: GherkinTagImpl?) {
        visitElement(gherkinTag)
    }

    fun visitStepParameter(gherkinStepParameter: GherkinStepParameterImpl?) {
        visitElement(gherkinStepParameter)
    }

    fun visitGherkinTableCell(cell: GherkinTableCell?) {
        visitElement(cell)
    }

    fun visitPystring(phstring: GherkinPystring?) {
        visitElement(phstring)
    }
}