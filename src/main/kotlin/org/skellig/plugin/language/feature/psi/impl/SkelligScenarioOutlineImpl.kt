package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.skellig.plugin.language.feature.psi.SkelligElementTypes
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligExamplesBlock
import org.skellig.plugin.language.feature.psi.SkelligScenarioOutline

class SkelligScenarioOutlineImpl(node: ASTNode) : SkelligStepsHolderBase(node), SkelligScenarioOutline {
    override fun toString(): String {
        return "SkelligScenarioOutline:$elementText"
    }

    override fun getPresentableText(): String? {
        return buildPresentableText("Scenario Outline")
    }

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitScenarioOutline(this)
    }

    override fun getExamplesBlocks(): List<SkelligExamplesBlock> {
        val result: MutableList<SkelligExamplesBlock> = ArrayList()
        val nodes = node.getChildren(EXAMPLES_BLOCK_FILTER)
        for (node in nodes) {
            result.add(node.psi as SkelligExamplesBlock)
        }
        return result
    }

    override fun getOutlineTableMap(): Map<String, String>? {
        return CachedValuesManager
            .getCachedValue(this) { CachedValueProvider.Result.create(buildOutlineTableMap(this), PsiModificationTracker.MODIFICATION_COUNT) }
    }

    companion object {
        private val EXAMPLES_BLOCK_FILTER = TokenSet.create(SkelligElementTypes.Companion.EXAMPLES_BLOCK)
        private fun buildOutlineTableMap(scenarioOutline: SkelligScenarioOutline?): Map<String, String>? {
            if (scenarioOutline == null) {
                return null
            }
            val examplesBlocks = scenarioOutline.getExamplesBlocks()
            for (examplesBlock in examplesBlocks) {
                val table = examplesBlock.getTable()
                if (table == null || table.headerRow == null || table.dataRows.size == 0) {
                    continue
                }
                val headerCells = table.headerRow?.psiCells
                val dataCells = table.dataRows[0]?.psiCells
                val result: MutableMap<String, String> = HashMap()
                for (i in headerCells!!.indices) {
                    if (i >= dataCells!!.size) {
                        break
                    }
                    result[headerCells[i].text.trim { it <= ' ' }] = dataCells[i].text.trim { it <= ' ' }
                }
                return result
            }
            return null
        }
    }
}