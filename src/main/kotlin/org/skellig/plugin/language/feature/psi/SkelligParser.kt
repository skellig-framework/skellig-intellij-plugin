package org.skellig.plugin.language.feature.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class SkelligParser : PsiParser {

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val marker = builder.mark()
        parseFileTopLevel(builder)
        marker.done(SkelligElementTypes.SKELLIG_FILE)
        return builder.treeBuilt
    }

    companion object {
        private val SCENARIO_END_TOKENS = TokenSet.create(
            SkelligTokenTypes.BACKGROUND_KEYWORD,
            SkelligTokenTypes.SCENARIO_KEYWORD,
            SkelligTokenTypes.RULE_KEYWORD,
            SkelligTokenTypes.FEATURE_KEYWORD
        )

        private fun parseFileTopLevel(builder: PsiBuilder) {
            while (!builder.eof()) {
                val tokenType = builder.tokenType
                if (tokenType === SkelligTokenTypes.FEATURE_KEYWORD) {
                    parseFeature(builder)
                } else if (tokenType === SkelligTokenTypes.TAG) {
                    parseTags(builder)
                } else {
                    builder.advanceLexer()
                }
            }
        }

        private fun parseFeature(builder: PsiBuilder) {
            val marker = builder.mark()
            assert(builder.tokenType === SkelligTokenTypes.FEATURE_KEYWORD)
            val featureEnd = builder.currentOffset + getTokenLength(builder.tokenText)
            var descMarker: PsiBuilder.Marker? = null
            while (true) {
                val tokenType = builder.tokenType
                if (tokenType === SkelligTokenTypes.TEXT && descMarker == null) {
                    if (hadLineBreakBefore(builder, featureEnd)) {
                        descMarker = builder.mark()
                    }
                }
                if (SkelligTokenTypes.SCENARIOS_KEYWORDS.contains(tokenType) || tokenType === SkelligTokenTypes.RULE_KEYWORD || tokenType === SkelligTokenTypes.BACKGROUND_KEYWORD || tokenType === SkelligTokenTypes.TAG) {
                    if (descMarker != null) {
                        descMarker.done(SkelligElementTypes.FEATURE_HEADER)
                        descMarker = null
                    }
                    parseFeatureElements(builder)
                    if (builder.tokenType === SkelligTokenTypes.FEATURE_KEYWORD) {
                        break
                    }
                }
                builder.advanceLexer()
                if (builder.eof()) break
            }
            descMarker?.done(SkelligElementTypes.FEATURE_HEADER)
            marker.done(SkelligElementTypes.FEATURE)
        }

        private fun hadLineBreakBefore(builder: PsiBuilder, prevTokenEnd: Int): Boolean {
            if (prevTokenEnd < 0) return false
            val precedingText = builder.originalText.subSequence(prevTokenEnd, builder.currentOffset).toString()
            return precedingText.contains("\n")
        }

        private fun parseTags(builder: PsiBuilder) {
            while (builder.tokenType === SkelligTokenTypes.TAG) {
                val tagMarker = builder.mark()
                builder.advanceLexer()
                tagMarker.done(SkelligElementTypes.TAG)
            }
        }

        private fun parseFeatureElements(builder: PsiBuilder) {
            var ruleMarker: PsiBuilder.Marker? = null
            while (builder.tokenType !== SkelligTokenTypes.FEATURE_KEYWORD && !builder.eof()) {
                if (builder.tokenType === SkelligTokenTypes.RULE_KEYWORD) {
                    ruleMarker?.done(SkelligElementTypes.RULE)
                    ruleMarker = builder.mark()
                    builder.advanceLexer()
                    if (builder.tokenType === SkelligTokenTypes.COLON) {
                        builder.advanceLexer()
                    } else {
                        break
                    }
                    while (builder.tokenType === SkelligTokenTypes.TEXT) {
                        builder.advanceLexer()
                    }
                }
                val marker = builder.mark()
                // tags
                parseTags(builder)

                // scenarios
                builder.advanceLexer()
                parseScenario(builder)
                marker.done(SkelligElementTypes.SCENARIO)
            }
            ruleMarker?.done(SkelligElementTypes.RULE)
        }

        private fun parseScenario(builder: PsiBuilder) {
            while (!atScenarioEnd(builder)) {
                if (builder.tokenType === SkelligTokenTypes.TAG) {
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
                if (builder.tokenType === SkelligTokenTypes.STEP_KEYWORD) {
                    parseStep(builder)
                } else if (builder.tokenType === SkelligTokenTypes.EXAMPLES_KEYWORD) {
                    parseExamplesBlock(builder)
                } else {
                    builder.advanceLexer()
                }
            }
        }

        private fun atScenarioEnd(builder: PsiBuilder): Boolean {
            var i = 0
            while (builder.lookAhead(i) === SkelligTokenTypes.TAG) {
                i++
            }
            val tokenType = builder.lookAhead(i)
            return tokenType == null || SCENARIO_END_TOKENS.contains(tokenType)
        }

        private fun parseStepParameter(builder: PsiBuilder): Boolean {
            if (builder.tokenType === SkelligTokenTypes.STEP_PARAMETER_TEXT) {
                val stepParameterMarker = builder.mark()
                builder.advanceLexer()
                stepParameterMarker.done(SkelligElementTypes.STEP_PARAMETER)
                return true
            }
            return false
        }

        private fun parseStep(builder: PsiBuilder) {
            val marker = builder.mark()
            builder.advanceLexer()
            var prevTokenEnd = -1
            while (builder.tokenType === SkelligTokenTypes.TEXT || builder.tokenType === SkelligTokenTypes.STEP_PARAMETER_BRACE || builder.tokenType === SkelligTokenTypes.STEP_PARAMETER_TEXT) {
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
            if (tokenTypeAfterName === SkelligTokenTypes.PIPE) {
                parseTable(builder)
            } else if (tokenTypeAfterName === SkelligTokenTypes.PYSTRING) {
                parsePystring(builder)
            }
            marker.done(SkelligElementTypes.STEP)
        }

        private fun parsePystring(builder: PsiBuilder) {
            if (!builder.eof()) {
                val marker = builder.mark()
                builder.advanceLexer()
                while (!builder.eof() && builder.tokenType !== SkelligTokenTypes.PYSTRING) {
                    if (!parseStepParameter(builder)) {
                        builder.advanceLexer()
                    }
                }
                if (!builder.eof()) {
                    builder.advanceLexer()
                }
                marker.done(SkelligElementTypes.PYSTRING)
            }
        }

        private fun parseExamplesBlock(builder: PsiBuilder) {
            val marker = builder.mark()
            builder.advanceLexer()
            if (builder.tokenType === SkelligTokenTypes.COLON) builder.advanceLexer()
            while (builder.tokenType === SkelligTokenTypes.TEXT) {
                builder.advanceLexer()
            }
            if (builder.tokenType === SkelligTokenTypes.PIPE) {
                parseTable(builder)
            }
            marker.done(SkelligElementTypes.EXAMPLES_BLOCK)
        }

        private fun parseTable(builder: PsiBuilder) {
            val marker = builder.mark()
            var rowMarker = builder.mark()
            var prevCellEnd = -1
            var isHeaderRow = true
            var cellMarker: PsiBuilder.Marker? = null
            var prevToken: IElementType? = null
            while (builder.tokenType === SkelligTokenTypes.PIPE || builder.tokenType === SkelligTokenTypes.TABLE_CELL) {
                val tokenType = builder.tokenType
                val hasLineBreakBefore = hadLineBreakBefore(builder, prevCellEnd)

                // cell - is all between pipes
                if (prevToken === SkelligTokenTypes.PIPE) {
                    // Don't start new cell if prev was last in the row
                    // it's not a cell, we just need to close a row
                    if (!hasLineBreakBefore) {
                        cellMarker = builder.mark()
                    }
                }
                if (tokenType === SkelligTokenTypes.PIPE) {
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
            marker.done(SkelligElementTypes.TABLE)
        }

        private fun closeCell(cellMarker: PsiBuilder.Marker) {
            cellMarker.done(SkelligElementTypes.TABLE_CELL)
        }

        private fun closeRowMarker(rowMarker: PsiBuilder.Marker, headerRow: Boolean) {
            rowMarker.done(if (headerRow) SkelligElementTypes.TABLE_HEADER_ROW else SkelligElementTypes.TABLE_ROW)
        }

        private fun getTokenLength(tokenText: String?): Int {
            return tokenText?.length ?: 0
        }
    }
}