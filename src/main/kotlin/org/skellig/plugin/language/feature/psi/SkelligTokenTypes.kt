package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.TokenSet

interface SkelligTokenTypes {
    companion object {
        val COMMENT = SkelligElementType("COMMENT")
        val TEXT = SkelligElementType("TEXT")
        val EXAMPLES_KEYWORD = SkelligElementType("EXAMPLES_KEYWORD")
        val FEATURE_KEYWORD = SkelligElementType("FEATURE_KEYWORD")
        val RULE_KEYWORD = SkelligElementType("RULE_KEYWORD")
        val BACKGROUND_KEYWORD = SkelligElementType("BACKGROUND_KEYWORD")
        val SCENARIO_KEYWORD = SkelligElementType("SCENARIO_KEYWORD")
        val EXAMPLE_KEYWORD = SkelligElementType("EXAMPLE_KEYWORD")
        val SCENARIO_OUTLINE_KEYWORD = SkelligElementType("SCENARIO_OUTLINE_KEYWORD")
        val STEP_KEYWORD = SkelligElementType("STEP_KEYWORD")
        val STEP_PARAMETER_BRACE = SkelligElementType("STEP_PARAMETER_BRACE")
        val STEP_PARAMETER_TEXT = SkelligElementType("STEP_PARAMETER_TEXT")
        val COLON = SkelligElementType("COLON")
        val TAG = SkelligElementType("TAG")
        val PYSTRING = SkelligElementType("PYSTRING_QUOTES")
        val PYSTRING_TEXT = SkelligElementType("PYSTRING_TEXT")
        val PIPE = SkelligElementType("PIPE")
        val TABLE_CELL = SkelligElementType("TABLE_CELL")
        val KEYWORDS = TokenSet.create(
            FEATURE_KEYWORD, RULE_KEYWORD, EXAMPLE_KEYWORD,
            BACKGROUND_KEYWORD, SCENARIO_KEYWORD, SCENARIO_OUTLINE_KEYWORD,
            EXAMPLES_KEYWORD, EXAMPLES_KEYWORD,
            STEP_KEYWORD
        )
        val SCENARIOS_KEYWORDS = TokenSet.create(SCENARIO_KEYWORD, SCENARIO_OUTLINE_KEYWORD, EXAMPLE_KEYWORD)
    }
}