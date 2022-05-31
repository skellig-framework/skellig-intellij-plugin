package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class SkelligTestStepParser : PsiParser {

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        builder.setDebugMode(true)
        val marker = builder.mark()
        parseFileTopLevel(builder)
        marker.done(SkelligTestStepElementTypes.FILE)
        return builder.treeBuilt
    }

    companion object {

        private val OPENING_BRACKETS_ERROR = "No opening bracket found"
        private val CLOSING_BRACKETS_ERROR = "No closing bracket found"
        private val OPENING_ARRAY_BRACKETS_ERROR = "No opening array bracket found"
        private val CLOSING_ARRAY_BRACKETS_ERROR = "No closing array bracket found"

        private fun parseFileTopLevel(builder: PsiBuilder) {
            while (!builder.eof()) {
                val tokenType = builder.tokenType
                if (tokenType === SkelligTestStepTokenTypes.NAME) {
                    parseTestStep(builder)
                } else {
                    builder.advanceLexer()
                }
            }
        }

        private fun parseTestStep(builder: PsiBuilder) {
            val marker = builder.mark()
            if (parseTestStepName(builder, marker)) {
                var isValidContent = true
                var tokenType: IElementType? = SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET
                var brackets = 0
                while (!builder.eof() && isValidContent && tokenType != SkelligTestStepTokenTypes.NAME) {
                    if (tokenType != TokenType.NEW_LINE_INDENT) {
                        tokenType = builder.tokenType

                        isValidContent = if (tokenType == SkelligTestStepTokenTypes.VARIABLES) {
                            parseTestStepComponent(builder, SkelligTestStepElementTypes.VARIABLES)
                        } else if (SkelligTestStepTokenTypes.REQUESTS.contains(tokenType)) {
                            parseTestStepComponent(builder, SkelligTestStepElementTypes.REQUEST)
                        } else if (SkelligTestStepTokenTypes.VALIDATIONS.contains(tokenType)) {
                            parseTestStepComponent(builder, SkelligTestStepElementTypes.VALIDATION)
                        } else if (tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                            brackets++
                            break
                        } else if (tokenType == SkelligTestStepTokenTypes.PROPERTY) {
                            if (parseFieldValue(builder)) continue
                            else false
                        } else {
                            true
                        }
                    }
                    if (isValidContent) {
                        builder.advanceLexer()
                        if (builder.tokenType != null) {
                            tokenType = builder.tokenType
                        }
                    }
                }
                if (isValidContent) {
                    if (brackets == 1 && tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                        marker.done(SkelligTestStepElementTypes.STEP)
                    } else {
                        if (brackets > 1) {
                            marker.error("No opening bracket found for the closed one")
                        } else {
                            marker.error(CLOSING_BRACKETS_ERROR)
                        }
                    }
                } else {
                    marker.drop()
                }
            }
        }

        private fun parseTestStepName(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
            assert(builder.tokenType === SkelligTestStepTokenTypes.NAME)
            builder.advanceLexer()
            var hasError = false
            if (builder.tokenType === SkelligTestStepTokenTypes.OPEN_BRACKET) {
                builder.advanceLexer()
                if (builder.tokenType === SkelligTestStepTokenTypes.TEXT) {
                    builder.advanceLexer()
                    if (builder.tokenType === SkelligTestStepTokenTypes.CLOSE_BRACKET) {
                        builder.advanceLexer()
                        if (builder.tokenType != SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                            marker.error("Missing opening curly bracket for the test step")
                            hasError = true
                        }
                    } else {
                        marker.error("Missing closing bracket for the test step")
                        hasError = true
                    }
                } else {
                    marker.error("Missing test step name")
                    hasError = true
                }
            } else {
                marker.error("Missing opening bracket for the test step")
                hasError = true
            }
            return !hasError
        }

        private fun parseTestStepComponent(builder: PsiBuilder, elementType: SkelligTestStepElementType): Boolean {
            val marker = builder.mark()
            builder.advanceLexer()
            return if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                builder.advanceLexer()
                if (parseTestData(builder)) {
                    if (builder.tokenType === SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                        marker.done(elementType)
                        true
                    } else {
                        marker.error(CLOSING_BRACKETS_ERROR)
                        false
                    }
                } else {
                    marker.drop()
                    false
                }
            } else if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET && elementType != SkelligTestStepTokenTypes.VARIABLES) {
                builder.advanceLexer()
                if (parseArrayTestData(builder)) {
                    if (builder.tokenType === SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
                        marker.done(elementType)
                        true
                    } else {
                        marker.error(CLOSING_ARRAY_BRACKETS_ERROR)
                        false
                    }
                } else {
                    marker.drop()
                    false
                }
            } else if (builder.tokenType == SkelligTestStepTokenTypes.EQUAL && elementType != SkelligTestStepTokenTypes.VARIABLES) {
                builder.advanceLexer()
                if (parseSingleValue(builder)) {
                    marker.done(elementType)
                    true
                } else {
                    marker.drop()
                    false
                }
            }
            else {
                marker.error("$elementType must have a value")
                false
            }
        }

        private fun parseTestData(builder: PsiBuilder): Boolean {
            var isValid = true
            while (!builder.eof() && isValid && builder.tokenType != SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                isValid = parseFieldValue(builder)
            }
            return isValid
        }

        private fun parseFieldValue(builder: PsiBuilder): Boolean {
            if (builder.tokenType == TokenType.NEW_LINE_INDENT) {
                builder.advanceLexer()
            } else {
                val marker = builder.mark()

                if (!SkelligTestStepElementTypes.elementsForToken.containsKey(builder.tokenType)) {
                    marker.error("Invalid character found instead of a property declaration")
                    return false
                } else {
                    val fieldMarker = builder.mark()
                    fieldMarker.done(SkelligTestStepElementTypes.elementsForToken[builder.tokenType]!!)
                    val fieldName = builder.tokenText
                    builder.advanceLexer()

                    if (!parseValue(fieldName, builder, marker)) {
                        builder.advanceLexer()
                        return false
                    } else {
                        builder.advanceLexer()
                    }
                }
            }
            return true
        }

        private fun parseValue(fieldName: String?, builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
            var isValid = true
            if (builder.tokenType == SkelligTestStepTokenTypes.EQUAL) {
                builder.advanceLexer()
                if (parseSingleValue(builder)) {
                    marker.done(SkelligTestStepElementTypes.FIELD_VALUE)
                } else {
                    marker.error("No value found for field '$fieldName' after '=' ")
                    isValid = false
                }
            } else if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                if (parseObjectTestData(builder)) {
                    marker.done(SkelligTestStepElementTypes.FIELD_VALUE)
                } else {
                    marker.drop()
                    isValid = false
                }
            } else if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET) {
                if (parseArrayTestData(builder)) {
                    marker.done(SkelligTestStepElementTypes.FIELD_VALUE)
                } else {
                    marker.drop()
                    isValid = false
                }
            } else {
                marker.error("No value found")
                isValid = false
            }
            return isValid
        }

        private fun parseObjectTestData(builder: PsiBuilder): Boolean {
            val marker = builder.mark()
            builder.advanceLexer()
            return if (parseTestData(builder)) {
                if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                    marker.done(SkelligTestStepElementTypes.OBJECT)
                    true
                } else {
                    marker.error(CLOSING_BRACKETS_ERROR)
                    false
                }
            } else {
                marker.drop()
                false
            }
        }

        private fun parseArrayTestData(builder: PsiBuilder): Boolean {
            builder.advanceLexer()
            var isContentValid = true
            val arrayMarker = builder.mark()
            while (isContentValid && builder.tokenType != null && builder.tokenType != SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
                if (SkelligTestStepElementTypes.elementsInArrayForToken.containsKey(builder.tokenType)) {
                    parseSingleValue(builder)
                } else if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                    if (parseObjectTestData(builder)) {
                        builder.advanceLexer()
                    } else {
                        isContentValid = false
                    }
                } else if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET) {
                    if (parseArrayTestData(builder)) {
                        builder.advanceLexer()
                    } else {
                        isContentValid = false
                    }
                } else if (builder.tokenType == TokenType.NEW_LINE_INDENT) {
                    builder.advanceLexer()
                } else {
                    arrayMarker.error("Invalid content in array. Must contain a value, object or array");
                    isContentValid = false
                }
            }
            return if (isContentValid) {
                if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
                    arrayMarker.done(SkelligTestStepElementTypes.ARRAY)
                    true
                } else {
                    arrayMarker.error(CLOSING_ARRAY_BRACKETS_ERROR)
                    false
                }
            } else {
                arrayMarker.drop()
                false
            }
        }

        private fun parseSingleValue(builder: PsiBuilder): Boolean {
            val valueMarker = builder.mark()
            var valueCounter = 0
            while (SkelligTestStepTokenTypes.VALUE_TOKENS.contains(builder.tokenType)) {
                val marker = builder.mark()
                marker.done(SkelligTestStepElementTypes.elementsForToken[builder.tokenType]!!)
                valueCounter++
                builder.advanceLexer()
            }
            valueMarker.done(SkelligTestStepElementTypes.VALUE)

            return valueCounter > 0
        }
    }
}