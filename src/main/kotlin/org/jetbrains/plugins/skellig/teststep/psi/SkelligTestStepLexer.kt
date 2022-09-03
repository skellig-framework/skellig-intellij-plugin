package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.util.ArrayUtil

class SkelligTestStepLexer(private val myKeywordProvider: SkelligTestStepKeywordProvider) : LexerBase() {

    private var myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE
    private var myStartOffset = 0
    private var myEndOffset = 0
    private var myPosition = 0
    private var myCurrentToken: IElementType? = null
    private var myCurrentTokenStart = 0
    private var myKeywords: Collection<String?>? = null
    private var myState = 0
    private var currentParametersCount = 0
    private var currentExpressionsCount = 0
    private var arraysCount = 0
    private var myCurLanguage: String? = null
    private var parameterIndexes: MutableSet<Int>? = null
    private var expressionIndexes: MutableSet<Int>? = null
    private var stringChars = setOf('\"', '\'')
    private var eofChars = setOf('\n', '\r')
    private var specialChars = setOf('(', ')', '{', '}', '[', ']', '.', '#', '$', ':', '=').union(stringChars).union(eofChars)
    private var propertyValueChars = setOf('=', '{', '[')
    private var parameterValueChars = setOf(':', '}', '$')
    private var expressionValueChars = setOf('#', ']')

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        myBuffer = buffer
        myStartOffset = startOffset
        myEndOffset = endOffset
        myPosition = startOffset
        myState = initialState
        parameterIndexes = mutableSetOf()
        expressionIndexes = mutableSetOf()
        myKeywords = myKeywordProvider.getAllKeywords()
        advance()
    }

    override fun getState(): Int {
        return myState
    }

    override fun getTokenType(): IElementType? {
        return myCurrentToken
    }

    override fun getTokenStart(): Int {
        return myCurrentTokenStart
    }

    override fun getTokenEnd(): Int {
        return myPosition
    }

    private fun isStepParameter(currentElementTerminator: String): Boolean {
        var pos = myPosition
        if (myBuffer[pos] == '<') {
            while (pos < myEndOffset && myBuffer[pos] != '\n' && myBuffer[pos] != '>' && !isStringAtPosition(currentElementTerminator, pos)) {
                pos++
            }
            return pos < myEndOffset && myBuffer[pos] == '>'
        }
        return false
    }

    override fun advance() {
        if (myPosition >= myEndOffset) {
            myCurrentToken = null
            return
        }
        myCurrentTokenStart = myPosition
        val c = myBuffer[myPosition]
        if (myState == SIMPLE_VALUE_STATE && isNewLineChar(c)) {
            advanceOverWhitespace()
            myCurrentToken = TokenType.NEW_LINE_INDENT
            while (myPosition < myEndOffset && isNewLineChar(myBuffer[myPosition])) {
                advanceOverWhitespace()
            }
        } else if (myState != STATE_INSIDE_STRING && Character.isWhitespace(c)) {
            advanceOverWhitespace(if (myState == ARRAY_STATE) myState else STATE_DEFAULT)
            myCurrentToken = TokenType.WHITE_SPACE
            while (myPosition < myEndOffset && Character.isSpaceChar(myBuffer[myPosition])) {
                advanceOverWhitespace(if (myState == ARRAY_STATE) myState else STATE_DEFAULT)
                if (myPosition < myEndOffset && isNewLineChar(myBuffer[myPosition])) {
                    myCurrentToken = TokenType.NEW_LINE_INDENT
                    break
                }
            }
        } else if (myState != STATE_PARAMETER && isStringAtPosition()) {
            myCurrentToken = SkelligTestStepTokenTypes.STRING_TEXT
            advanceToNextSpecialChar(stringChars, myState, false)
            myPosition++
        } else if (isCommentAtPosition() && myState != STATE_INSIDE_STRING) {
            myCurrentToken = SkelligTestStepTokenTypes.COMMENT
            advanceToNextSpecialChar(eofChars)
        } else if (c == '.' && myState != STATE_INSIDE_STRING) {
            myCurrentToken = SkelligTestStepTokenTypes.DOT
            myPosition++
        } else if (c == '=' && myState != STATE_INSIDE_STRING) {
            myCurrentToken = SkelligTestStepTokenTypes.EQUAL
            myState = SIMPLE_VALUE_STATE
            myPosition++
        } else if (c == '(' && (myState == STATE_DEFAULT || myState == STATE_TEST_STEP)) {
            myCurrentToken = SkelligTestStepTokenTypes.OPEN_BRACKET
            myPosition++
        } else if (c == ')' && (myState == STATE_DEFAULT || myState == STATE_TEST_STEP)) {
            myCurrentToken = SkelligTestStepTokenTypes.CLOSE_BRACKET
            myPosition++
        }
        // Set state as a starting of a parameter
        else if (isParameterAtPosition()) {
            myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_OPEN_BRACKET
            myPosition += 2
            myState = STATE_PARAMETER
            currentParametersCount++
        }
        else if (myState == STATE_PARAMETER_DEFAULT) {
            when (c) {
                '}' -> {
                    myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_CLOSE_BRACKET
                    myPosition++
                    currentParametersCount--
                    myState = if (currentParametersCount == 0) SIMPLE_VALUE_STATE else STATE_PARAMETER
                }
                else -> {
                    myCurrentToken = SkelligTestStepTokenTypes.TEXT
                    advanceToNextSpecialChar(specialChars, STATE_PARAMETER_DEFAULT)
                }
            }
        }
        // Process parameter
        else if (currentParametersCount > 0 || myState == STATE_PARAMETER) {
            // examples:
            // ${key}
            // ${key:}
            // ${key: default}
            // ${key: ${key_2}}
            when (c) {
                '}' -> {
                    myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_CLOSE_BRACKET
                    myPosition++
                    currentParametersCount--
                    myState = if (currentParametersCount == 0)
                        if (arraysCount > 0) ARRAY_STATE else SIMPLE_VALUE_STATE
                    else STATE_PARAMETER
                }
                ':' -> {
                    myCurrentToken = SkelligTestStepTokenTypes.COLON
                    myState = STATE_PARAMETER_DEFAULT
                    myPosition++
                }
                else -> {
                    myCurrentToken = SkelligTestStepTokenTypes.PARAMETER
                    advanceToNextSpecialChar(parameterValueChars, STATE_PARAMETER)
                }
            }
        } else if ((c == '{' || c == '}') && myState != STATE_INSIDE_STRING) {
            myCurrentToken = if (c == '{') SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET else SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET
            if (state == ARRAY_STATE) {
                myState = STATE_DEFAULT
            }
            myPosition++
        }

        // Set state as a starting of an expression
        else if (isExpressionAtPosition()) {
            myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION_OPEN_BRACKET
            myPosition += 2
            myState = STATE_EXPRESSION
            currentExpressionsCount++
        }
        // Process expression
        else if (currentExpressionsCount > 0 || myState == STATE_EXPRESSION) {
            // example: #[a.b.subString(1,2)]
            when (c) {
                ']' -> {
                    myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION_CLOSE_BRACKET
                    myPosition++
                    currentExpressionsCount--
                    myState = if (currentExpressionsCount == 0)
                        if (arraysCount > 0) ARRAY_STATE else SIMPLE_VALUE_STATE
                    else STATE_EXPRESSION
                }
                else -> {
                    myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION
                    advanceToNextSpecialChar(expressionValueChars.union(parameterValueChars), STATE_EXPRESSION)
                }
            }
        }
        else if ((c == '[' || c == ']') && myState != STATE_INSIDE_STRING) {
            myCurrentToken = if (c == '[') {
                arraysCount++
                myState = ARRAY_STATE
                SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET
            } else {
                arraysCount--
                myState = STATE_DEFAULT
                SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET
            }
            myPosition++
        } else if (myState == SIMPLE_VALUE_STATE) {
            myCurrentToken = SkelligTestStepTokenTypes.TEXT
            advanceToNextSpecialChar(specialChars, SIMPLE_VALUE_STATE)
        } else {
            if (myState == STATE_DEFAULT) {
                if (myState == STATE_DEFAULT && isStringAtPosition(SkelligTestStepTokenTypes.NAME.toString().toLowerCase())) {
                    myCurrentToken = SkelligTestStepTokenTypes.NAME
                    myPosition += myCurrentToken.toString().length
                    myState = STATE_TEST_STEP
                    return
                } else {
                    for (keyword in myKeywords!!) {
                        if (isStringAtPosition(keyword!!)) {
                            val length = keyword.length
                            myCurrentToken = myKeywordProvider.getTokenType(keyword)
                            myPosition += length
                            myState = STATE_AFTER_KEYWORD
                            return
                        }
                    }
                }
            }
            myCurrentToken = if (myState == STATE_DEFAULT && isProperty()) {
                advanceToNextSpecialChar(specialChars, PROPERTY_STATE)
                SkelligTestStepTokenTypes.PROPERTY
            } else {
                advanceToNextSpecialChar(specialChars, if (myState == ARRAY_STATE) myState else STATE_DEFAULT)
                SkelligTestStepTokenTypes.TEXT
            }
        }
    }

    private fun isProperty(): Boolean {
        var position = myPosition
        //TODO: Ignore propertyValueChars in quotes, params and expression
        while (position < myEndOffset - 1 && !propertyValueChars.contains(myBuffer[position])) {
            position++
        }
        return if (myBuffer[position] == '=') {
            var backPosition = position - 1
            while (backPosition >= 0 && eofChars.contains(myBuffer[backPosition])) {
                backPosition--
            }
            backPosition == position - 1
        } else {
            propertyValueChars.contains(myBuffer[position])
        }
    }

    private fun isNewLineChar(c: Char) = eofChars.contains(c)

    private fun processEnclosedInBrackets(openingSymbol: Char, openingBracket: Char, closingBracket: Char, indexesMemory: MutableSet<Int>?) {
        var counter = 1
        var position = ++myPosition
        if (position < myEndOffset && myBuffer[position] == openingBracket) {
            while (counter > 0 && position++ < myEndOffset) {
                if (myBuffer[position] == '\\') {
                    val nextPos = position + 1
                    if (nextPos < myEndOffset) {
                        val nextChar = myBuffer[nextPos]
                        if (nextChar == openingBracket || nextChar == closingBracket || nextChar == '\\') {
                            position += 2
                            continue
                        }
                    }
                } else if (myBuffer[position - 1] == openingSymbol && myBuffer[position] == openingBracket) {
                    counter++
                    indexesMemory?.add(position)
                } else if (myBuffer[position] == closingBracket) {
                    counter--
                    indexesMemory?.add(position)
                } else if (myBuffer[position] == '\n') {
                    counter = 0
                }
            }
        }
    }

    private fun processFunction(): Boolean {
        var isFunction = false
        var position = myPosition
        while (position++ < myEndOffset && position < myBuffer.length &&
            myBuffer[position] != ' ' &&
            !propertyValueChars.contains(myBuffer[position]) &&
            !stringChars.contains(myBuffer[position]) &&
            !eofChars.contains(myBuffer[position])
        ) {
            if (!isFunction && myBuffer[position - 1] != '\\' && myBuffer[position] == '(') {
                isFunction = true
            } else if (isFunction && myBuffer[position - 1] != '\\' && myBuffer[position] == ')') {
                myPosition = position
                return true
            }
        }
        return false
    }

    private fun isParameterAtPosition(): Boolean {
        return is_previous_not_backslash() &&
                myBuffer.length > myPosition + 1 && myBuffer[myPosition] == '$' && myBuffer[myPosition + 1] == '{'
    }

    private fun isExpressionAtPosition(): Boolean {
        return is_previous_not_backslash() &&
                myBuffer.length > myPosition + 1 && myBuffer[myPosition] == '#' && myBuffer[myPosition + 1] == '['
    }

    private fun advanceOverWhitespace(nextState: Int = STATE_DEFAULT) {
        if (myBuffer[myPosition] == '\n') {
            myState = nextState
        }
        myPosition++
    }

    private fun isStringAtPosition(keyword: String): Boolean {
        val length = keyword.length
        return myEndOffset - myPosition >= length && myBuffer.subSequence(myPosition, myPosition + length).toString() == keyword
    }

    private fun isStringAtPosition(keyword: String, position: Int): Boolean {
        val length = keyword.length
        return myEndOffset - position >= length && myBuffer.subSequence(position, position + length).toString() == keyword
    }

    private fun isStringAtPosition(): Boolean {
        return (myBuffer[myPosition] == '\'' || myBuffer[myPosition] == '"') && is_previous_not_backslash()

    }

    private fun is_previous_not_backslash() = (myPosition > 0 && myBuffer.length > 1 && myBuffer[myPosition - 1] != '\\')

    private fun isCommentAtPosition(): Boolean {
        return myBuffer.length > myPosition + 1 && myBuffer[myPosition] == '/' && myBuffer[myPosition + 1] == '/'
    }

    private fun advanceToNextSpecialChar(specialChars: Set<Char>, nextState: Int = STATE_DEFAULT, shouldReturnWhitespace: Boolean = true) {
        myPosition++
        val mark = myPosition
        while (myPosition < myEndOffset) {
            // ignore if character is marked with `\` meaning that it's not a special char
            if (myBuffer[myPosition - 1] != '\\' && specialChars.contains(myBuffer[myPosition])) break
            else myPosition++
        }

        if (shouldReturnWhitespace) returnWhitespace(mark)
        myState = nextState
    }

    private fun returnWhitespace(mark: Int) {
        while (myPosition > mark && Character.isWhitespace(myBuffer[myPosition - 1])) {
            myPosition--
        }
    }

    private fun advanceToParameterOrSymbol(s: String, parameterState: Int, shouldReturnWhitespace: Boolean) {
        val mark = myPosition
        while (myPosition < myEndOffset && !isStringAtPosition(s) && !isStepParameter(s)) {
            myPosition++
        }
        if (shouldReturnWhitespace) {
            myState = STATE_DEFAULT
            if (myPosition < myEndOffset) {
                if (!isStringAtPosition(s)) {
                    myState = parameterState
                }
            }
            returnWhitespace(mark)
        }
    }

    override fun getBufferSequence(): CharSequence {
        return myBuffer
    }

    override fun getBufferEnd(): Int {
        return myEndOffset
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_AFTER_KEYWORD = 1
        private const val STATE_PARAMETER = 2
        private const val STATE_PARAMETER_DEFAULT = 3
        private const val STATE_EXPRESSION = 4
        private const val STATE_FUNCTION = 5
        private const val STATE_INSIDE_STRING = 6
        private const val STATE_TEST_STEP = 7
        private const val SIMPLE_VALUE_STATE = 8
        private const val PROPERTY_STATE = 9
        private const val ARRAY_STATE = 10
        private const val STRING_MARKER = "\""
    }
}