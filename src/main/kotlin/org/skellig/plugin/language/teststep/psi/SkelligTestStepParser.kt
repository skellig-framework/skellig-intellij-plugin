package org.skellig.plugin.language.teststep.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class SkelligTestStepParser : PsiParser {

    private val OPENING_BRACKETS_ERROR = "No opening bracket found"
    private val CLOSING_BRACKETS_ERROR = "No closing bracket found"
    private val OPENING_ARRAY_BRACKETS_ERROR = "No opening array bracket found"
    private val CLOSING_ARRAY_BRACKETS_ERROR = "No closing array bracket found"
    private var objectBrackets = 0
    private var arrayBrackets = 0

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        builder.setDebugMode(true)
        val marker = builder.mark()
        parseFileTopLevel(builder)
        marker.done(SkelligTestStepElementTypes.FILE)
        return builder.treeBuilt
    }

    private fun parseFileTopLevel(builder: PsiBuilder): Boolean {
        val isValidContent = true
        while (!builder.eof()) {
            val tokenType = builder.tokenType
            if (tokenType === SkelligTestStepTokenTypes.NAME) {
                parseTestStep(builder)
            } else {
                advanceLexer(builder)
            }
        }
        return isValidContent
    }

    private fun parseTestStep(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        var isValidContent = parseTestStepName(builder, marker)
        if (isValidContent) {
            var tokenType: IElementType? = null
            while (!builder.eof() && isValidContent && tokenType != SkelligTestStepTokenTypes.NAME) {
                if (tokenType != TokenType.NEW_LINE_INDENT) {
                    tokenType = builder.tokenType

                    isValidContent = if (tokenType == SkelligTestStepTokenTypes.VARIABLES) {
                        parseTestStepComponent(builder, SkelligTestStepElementTypes.VARIABLES)
                    } else if (SkelligTestStepTokenTypes.REQUESTS.contains(tokenType)) {
                        parseTestStepComponent(builder, SkelligTestStepElementTypes.REQUEST)
                    } else if (SkelligTestStepTokenTypes.VALIDATIONS.contains(tokenType)) {
                        parseTestStepComponent(builder, SkelligTestStepElementTypes.VALIDATION)
                    } else if (tokenType == SkelligTestStepTokenTypes.PROPERTY ||
                        tokenType == SkelligTestStepTokenTypes.STRING_TEXT ||
                        tokenType == SkelligTestStepTokenTypes.PARAMETER_OPEN_BRACKET
                    ) {
                        if (parseFieldValue(builder)) continue
                        else false
                    } else if (tokenType == SkelligTestStepTokenTypes.ID) {
                        parseTestStepComponent(builder, SkelligTestStepElementTypes.ID)
                    } else tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET
                            || tokenType == TokenType.WHITE_SPACE || tokenType == TokenType.NEW_LINE_INDENT
                }
                if (isValidContent) {
                    if (tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET ||
                        tokenType == SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET
                        || tokenType == TokenType.WHITE_SPACE || tokenType == TokenType.NEW_LINE_INDENT
                    ) {
                        advanceLexer(builder)
                    }
                    if (builder.tokenType != null) {
                        tokenType = builder.tokenType
                    }
                }
            }

            if (isValidContent) {
                if (!markErrorIfInvalidBracketsCount(marker)) {
                    marker.done(SkelligTestStepElementTypes.STEP)
                }
            } else {
                advanceToNextTestStep(builder)
                if (!markErrorIfInvalidBracketsCount(marker)) {
                    marker.error("Property must be declared in the test step")
                }
            }
        }
        return isValidContent
    }

    private fun advanceToNextTestStep(builder: PsiBuilder) {
        while (!builder.eof() && builder.tokenType != SkelligTestStepTokenTypes.NAME) {
            advanceLexer(builder)
        }
    }

    private fun parseTestStepName(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        assert(builder.tokenType === SkelligTestStepTokenTypes.NAME)
        advanceLexer(builder)
        var hasError = false
        if (builder.tokenType === SkelligTestStepTokenTypes.OPEN_BRACKET) {
            advanceLexer(builder)
            if (builder.tokenType === SkelligTestStepTokenTypes.STRING_TEXT || builder.tokenType === SkelligTestStepTokenTypes.TEXT) {
                advanceLexer(builder)
                if (builder.tokenType === SkelligTestStepTokenTypes.CLOSE_BRACKET) {
                    advanceLexer(builder)
                    if (builder.tokenType != SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                        marker.error("Missing opening curly bracket for the test step")
                        hasError = true
                    } else {
                        advanceLexer(builder)
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
        advanceLexer(builder)
        return if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
            advanceLexer(builder)
            if (parseTestData(builder)) {
                if (builder.tokenType === SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                    advanceLexer(builder)
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
            advanceLexer(builder)
            if (parseArrayTestData(builder)) {
                marker.done(elementType)
                true
            } else {
                marker.drop()
                false
            }
        } else if (builder.tokenType == SkelligTestStepTokenTypes.EQUAL && elementType != SkelligTestStepTokenTypes.VARIABLES) {
            advanceLexer(builder)
            if (parseSingleValue(builder)) {
                marker.done(elementType)
                true
            } else {
                marker.drop()
                false
            }
        } else {
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
            advanceLexer(builder)
        } else {
            var marker = builder.mark()

            if (!(SkelligTestStepElementTypes.elementsForToken.containsKey(builder.tokenType) ||
                builder.tokenType == SkelligTestStepTokenTypes.PARAMETER_OPEN_BRACKET)) {
                marker.error("Invalid character found instead of a property declaration")
                return false
            } else {
                var fieldName = builder.tokenText
                advanceLexer(builder)

                while (SkelligTestStepTokenTypes.PROPERTY_TOKENS.contains(builder.tokenType)) {
                    fieldName += builder.tokenText
                    marker = checkIfPropertyAndMark(builder, marker, SkelligTestStepElementTypes.PROPERTY)
                    advanceLexer(builder)
                }
                if (!parseValue(fieldName, builder, marker)) {
                    advanceLexer(builder)
                    return false
                } else {
                    //advanceLexer(builder)
                }
            }
        }
        return true
    }

    private fun parseValue(fieldName: String?, builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        var isValid = true
        if (builder.tokenType == SkelligTestStepTokenTypes.EQUAL) {
            advanceLexer(builder)
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
        advanceLexer(builder)
        return if (parseTestData(builder)) {
            if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET) {
                advanceLexer(builder)
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
        var isContentValid = true
        return if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
            advanceLexer(builder)
            isContentValid
        } else {
            val arrayMarker = builder.mark()
            advanceLexer(builder)
            while (isContentValid && builder.tokenType != null && builder.tokenType != SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
                if (SkelligTestStepElementTypes.elementsInArrayForToken.containsKey(builder.tokenType)) {
                    if (parseSingleValue(builder)) {
                       // advanceLexer(builder)
                    } else {
                        isContentValid = false
                    }
                } else if (builder.tokenType == SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET) {
                    isContentValid = parseObjectTestData(builder)
                } else if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET) {
                    isContentValid = parseArrayTestData(builder)
                } else if (builder.tokenType == TokenType.NEW_LINE_INDENT) {
                    advanceLexer(builder)
                } else {
                    arrayMarker.error("Invalid content in array. Must contain a value, object or array");
                    isContentValid = false
                }
            }
            if (isContentValid) {
                if (builder.tokenType == SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET) {
                    advanceLexer(builder)
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
    }

    private fun parseSingleValue(builder: PsiBuilder): Boolean {
        var valueMarker = builder.mark()
        var valueCounter = 0
        while (SkelligTestStepTokenTypes.VALUE_TOKENS.contains(builder.tokenType)) {
            valueCounter++
            valueMarker = checkIfPropertyAndMark(builder, valueMarker, SkelligTestStepElementTypes.VALUE)
            if (!SkelligTestStepTokenTypes.VALUE_CLOSING_BRACKETS.contains(builder.lookAhead(1))) advanceLexer(builder)
            else break
        }
        advanceLexer(builder)
        valueMarker.done(SkelligTestStepElementTypes.VALUE)

        return valueCounter > 0
    }

    private fun markErrorIfInvalidBracketsCount(marker: PsiBuilder.Marker): Boolean {
        return if (objectBrackets != 0) {
            marker.error("Invalid number of opening and closing curly brackets")
            true
        } else if (arrayBrackets != 0) {
            marker.error("Invalid number of opening and closing array brackets")
            true
        } else {
            false
        }
    }

    private fun checkIfPropertyAndMark(builder: PsiBuilder, marker: PsiBuilder.Marker, currentType: SkelligTestStepElementType): PsiBuilder.Marker {
        return when (builder.tokenType) {
            SkelligTestStepTokenTypes.PARAMETER -> {
                marker.done(currentType)
                builder.mark()
            }
            SkelligTestStepTokenTypes.PARAMETER_CLOSE_BRACKET -> {
                marker.done(SkelligTestStepElementTypes.PARAMETER)
                builder.mark()
            }
            else -> marker
        }
    }

    private fun advanceLexer(builder: PsiBuilder) {
        builder.advanceLexer()
        when (builder.tokenType) {
            SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET -> objectBrackets++
            SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET -> objectBrackets--
            SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET -> arrayBrackets++
            SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET -> arrayBrackets--
        }
    }
}