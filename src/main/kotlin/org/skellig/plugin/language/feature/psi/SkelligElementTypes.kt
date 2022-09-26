package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

interface SkelligElementTypes {
    companion object {
        val SKELLIG_FILE = IFileElementType(SkelligLanguage.INSTANCE)
        val FEATURE: IElementType = SkelligElementType("feature")
        val FEATURE_HEADER: IElementType = SkelligElementType("feature header")
        val STEP: IElementType = SkelligElementType("step")
        val STEP_PARAMETER: IElementType = SkelligElementType("step parameter")
        val SCENARIO: IElementType = SkelligElementType("scenario")
        val RULE: IElementType = SkelligElementType("rule")
        val EXAMPLES_BLOCK: IElementType = SkelligElementType("examples block")
        val TABLE: IElementType = SkelligElementType("table")
        val TABLE_HEADER_ROW: IElementType = SkelligElementType("table header row")
        val TABLE_ROW: IElementType = SkelligElementType("table row")
        val TABLE_CELL: IElementType = SkelligElementType("table cell")
        val TAG: IElementType = SkelligElementType("tag")
        val PYSTRING: IElementType = SkelligElementType("pystring")
        val SCENARIOS = TokenSet.create(SCENARIO)
    }
}