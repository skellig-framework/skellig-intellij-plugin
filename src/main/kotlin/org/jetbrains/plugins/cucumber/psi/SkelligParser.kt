package org.jetbrains.plugins.cucumber.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class SkelligParser : PsiParser {

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val marker = builder.mark()
        parseFileTopLevel(builder)
        marker.done(GherkinElementTypes.Companion.GHERKIN_FILE)
        return builder.treeBuilt
    }

    companion object {
        private val SCENARIO_END_TOKENS = TokenSet.create(
            GherkinTokenTypes.Companion.BACKGROUND_KEYWORD,
            GherkinTokenTypes.Companion.SCENARIO_KEYWORD,
            GherkinTokenTypes.Companion.SCENARIO_OUTLINE_KEYWORD,
            GherkinTokenTypes.Companion.RULE_KEYWORD,
            GherkinTokenTypes.Companion.FEATURE_KEYWORD
        )

        private fun parseFileTopLevel(builder: PsiBuilder) {
            while (!builder.eof()) {
                val tokenType = builder.tokenType
                if (tokenType === GherkinTokenTypes.Companion.FEATURE_KEYWORD) {
                    parseFeature(builder)
                } else if (tokenType === GherkinTokenTypes.Companion.TAG) {
                    parseTags(builder)
                } else {
                    builder.advanceLexer()
                }
            }
        }

        private fun parseFeature(builder: PsiBuilder) {
            val marker = builder.mark()
            assert(builder.tokenType === GherkinTokenTypes.Companion.FEATURE_KEYWORD)
            val featureEnd = builder.currentOffset + getTokenLength(builder.tokenText)
            var descMarker: PsiBuilder.Marker? = null
            while (true) {
                val tokenType = builder.tokenType
                if (tokenType === GherkinTokenTypes.Companion.TEXT && descMarker == null) {
                    if (hadLineBreakBefore(builder, featureEnd)) {
                        descMarker = builder.mark()
                    }
                }
                if (GherkinTokenTypes.Companion.SCENARIOS_KEYWORDS.contains(tokenType) || tokenType === GherkinTokenTypes.Companion.RULE_KEYWORD || tokenType === GherkinTokenTypes.Companion.BACKGROUND_KEYWORD || tokenType === GherkinTokenTypes.Companion.TAG) {
                    if (descMarker != null) {
                        descMarker.done(GherkinElementTypes.Companion.FEATURE_HEADER)
                        descMarker = null
                    }
                    parseFeatureElements(builder)
                    if (builder.tokenType === GherkinTokenTypes.Companion.FEATURE_KEYWORD) {
                        break
                    }
                }
                builder.advanceLexer()
                if (builder.eof()) break
            }
            descMarker?.done(GherkinElementTypes.Companion.FEATURE_HEADER)
            marker.done(GherkinElementTypes.Companion.FEATURE)
        }

        private fun hadLineBreakBefore(builder: PsiBuilder, prevTokenEnd: Int): Boolean {
            if (prevTokenEnd < 0) return false
            val precedingText = builder.originalText.subSequence(prevTokenEnd, builder.currentOffset).toString()
            return precedingText.contains("\n")
        }

        private fun parseTags(builder: PsiBuilder) {
            while (builder.tokenType === GherkinTokenTypes.Companion.TAG) {
                val tagMarker = builder.mark()
                builder.advanceLexer()
                tagMarker.done(GherkinElementTypes.Companion.TAG)
            }
        }

        private fun parseFeatureElements(builder: PsiBuilder) {
            var ruleMarker: PsiBuilder.Marker? = null
            while (builder.tokenType !== GherkinTokenTypes.Companion.FEATURE_KEYWORD && !builder.eof()) {
                if (builder.tokenType === GherkinTokenTypes.Companion.RULE_KEYWORD) {
                    ruleMarker?.done(GherkinElementTypes.Companion.RULE)
                    ruleMarker = builder.mark()
                    builder.advanceLexer()
                    if (builder.tokenType === GherkinTokenTypes.Companion.COLON) {
                        builder.advanceLexer()
                    } else {
                        break
                    }
                    while (builder.tokenType === GherkinTokenTypes.Companion.TEXT) {
                        builder.advanceLexer()
                    }
                }
                val marker = builder.mark()
                // tags
                parseTags(builder)

                // scenarios
                val startTokenType = builder.tokenType
                val outline = startTokenType === GherkinTokenTypes.Companion.SCENARIO_OUTLINE_KEYWORD
                builder.advanceLexer()
                parseScenario(builder)
                marker.done(if (outline) GherkinElementTypes.Companion.SCENARIO_OUTLINE else GherkinElementTypes.Companion.SCENARIO)
            }
            ruleMarker?.done(GherkinElementTypes.Companion.RULE)
        }

        private fun parseScenario(builder: PsiBuilder) {
            while (!atScenarioEnd(builder)) {
                if (builder.tokenType === GherkinTokenTypes.Companion.TAG) {
                    val marker = builder.mark()
                    parseTags(builder)
                    if (atScenarioEnd(builder)) {
                        marker.rollbackTo()
                        break
                    } else {
                        marker.drop()
                    }
                }
                if (parseStepParameter(builder)) {
                    continue
                }
                if (builder.tokenType === GherkinTokenTypes.Companion.STEP_KEYWORD) {
                    parseStep(builder)
                } else if (builder.tokenType === GherkinTokenTypes.Companion.EXAMPLES_KEYWORD) {
                    parseExamplesBlock(builder)
                } else {
                    builder.advanceLexer()
                }
            }
        }

        private fun atScenarioEnd(builder: PsiBuilder): Boolean {
            var i = 0
            while (builder.lookAhead(i) === GherkinTokenTypes.Companion.TAG) {
                i++
            }
            val tokenType = builder.lookAhead(i)
            return tokenType == null || SCENARIO_END_TOKENS.contains(tokenType)
        }

        private fun parseStepParameter(builder: PsiBuilder): Boolean {
            if (builder.tokenType === GherkinTokenTypes.Companion.STEP_PARAMETER_TEXT) {
                val stepParameterMarker = builder.mark()
                builder.advanceLexer()
                stepParameterMarker.done(GherkinElementTypes.Companion.STEP_PARAMETER)
                return true
            }
            return false
        }

        private fun parseStep(builder: PsiBuilder) {
            val marker = builder.mark()
            builder.advanceLexer()
            var prevTokenEnd = -1
            while (builder.tokenType === GherkinTokenTypes.Companion.TEXT || builder.tokenType === GherkinTokenTypes.Companion.STEP_PARAMETER_BRACE || builder.tokenType === GherkinTokenTypes.Companion.STEP_PARAMETER_TEXT) {
                val tokenText = builder.tokenText
                if (hadLineBreakBefore(builder, prevTokenEnd)) {
                    break
                }
                prevTokenEnd = builder.currentOffset + getTokenLength(tokenText)
                if (!parseStepParameter(builder)) {
                    builder.advanceLexer()
                }
            }
            val tokenTypeAfterName = builder.tokenType
            if (tokenTypeAfterName === GherkinTokenTypes.Companion.PIPE) {
                parseTable(builder)
            } else if (tokenTypeAfterName === GherkinTokenTypes.Companion.PYSTRING) {
                parsePystring(builder)
            }
            marker.done(GherkinElementTypes.Companion.STEP)
        }

        private fun parsePystring(builder: PsiBuilder) {
            if (!builder.eof()) {
                val marker = builder.mark()
                builder.advanceLexer()
                while (!builder.eof() && builder.tokenType !== GherkinTokenTypes.Companion.PYSTRING) {
                    if (!parseStepParameter(builder)) {
                        builder.advanceLexer()
                    }
                }
                if (!builder.eof()) {
                    builder.advanceLexer()
                }
                marker.done(GherkinElementTypes.Companion.PYSTRING)
            }
        }

        private fun parseExamplesBlock(builder: PsiBuilder) {
            val marker = builder.mark()
            builder.advanceLexer()
            if (builder.tokenType === GherkinTokenTypes.Companion.COLON) builder.advanceLexer()
            while (builder.tokenType === GherkinTokenTypes.Companion.TEXT) {
                builder.advanceLexer()
            }
            if (builder.tokenType === GherkinTokenTypes.Companion.PIPE) {
                parseTable(builder)
            }
            marker.done(GherkinElementTypes.Companion.EXAMPLES_BLOCK)
        }

        private fun parseTable(builder: PsiBuilder) {
            val marker = builder.mark()
            var rowMarker = builder.mark()
            var prevCellEnd = -1
            var isHeaderRow = true
            var cellMarker: PsiBuilder.Marker? = null
            var prevToken: IElementType? = null
            while (builder.tokenType === GherkinTokenTypes.Companion.PIPE || builder.tokenType === GherkinTokenTypes.Companion.TABLE_CELL) {
                val tokenType = builder.tokenType
                val hasLineBreakBefore = hadLineBreakBefore(builder, prevCellEnd)

                // cell - is all between pipes
                if (prevToken === GherkinTokenTypes.Companion.PIPE) {
                    // Don't start new cell if prev was last in the row
                    // it's not a cell, we just need to close a row
                    if (!hasLineBreakBefore) {
                        cellMarker = builder.mark()
                    }
                }
                if (tokenType === GherkinTokenTypes.Companion.PIPE) {
                    if (cellMarker != null) {
                        closeCell(cellMarker)
                        cellMarker = null
                    }
                }
                if (hasLineBreakBefore) {
                    closeRowMarker(rowMarker, isHeaderRow)
                    isHeaderRow = false
                    rowMarker = builder.mark()
                }
                prevCellEnd = builder.currentOffset + getTokenLength(builder.tokenText)
                prevToken = tokenType
                builder.advanceLexer()
            }
            if (cellMarker != null) {
                closeCell(cellMarker)
            }
            closeRowMarker(rowMarker, isHeaderRow)
            marker.done(GherkinElementTypes.Companion.TABLE)
        }

        private fun closeCell(cellMarker: PsiBuilder.Marker) {
            cellMarker.done(GherkinElementTypes.Companion.TABLE_CELL)
        }

        private fun closeRowMarker(rowMarker: PsiBuilder.Marker, headerRow: Boolean) {
            rowMarker.done(if (headerRow) GherkinElementTypes.Companion.TABLE_HEADER_ROW else GherkinElementTypes.Companion.TABLE_ROW)
        }

        private fun getTokenLength(tokenText: String?): Int {
            return tokenText?.length ?: 0
        }
    }
}