package org.skellig.plugin.language.teststep.psi.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.util.ArrayUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKeywordProvider
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes

class SkelligTestStepLexer(internal val myKeywordProvider: SkelligTestStepKeywordProvider) : LexerBase() {

    companion object {
        internal const val STATE_DEFAULT = 0
        internal const val STATE_PARAMETER = 1
        internal const val STATE_PARAMETER_DEFAULT = 2
        internal const val STATE_EXPRESSION = 3
        internal const val STATE_FUNCTION = 4
        internal const val STATE_INSIDE_STRING = 5
        internal const val STATE_TEST_STEP = 6
        internal const val SIMPLE_VALUE_STATE = 7
        internal const val PROPERTY_STATE = 8
        internal const val ARRAY_STATE = 9
    }

    private var charProcessors = mutableListOf<LexerCharProcessor>()
    internal var myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE
    private var myStartOffset = 0
    internal var myEndOffset = 0
    internal var myPosition = 0
    internal var myCurrentToken: IElementType? = null
    private var myCurrentTokenStart = 0
    internal var myKeywords: Collection<String?>? = null
    internal var myState = 0
    internal var currentParametersCount = 0
    internal var currentExpressionsCount = 0
    internal var arraysCount = 0
    private var parameterIndexes: MutableSet<Int>? = null
    private var expressionIndexes: MutableSet<Int>? = null
    internal var stringChars = setOf('\"', '\'')
    internal var eofChars = setOf('\n', '\r')
    internal var specialChars = setOf('(', ')', '{', '}', '[', ']', '.', '#', '$', ':', '=').union(stringChars).union(eofChars)
    internal var propertyValueChars = setOf('=', '{', '[')
    internal var afterKeywordChars = setOf(' ').union(propertyValueChars).union(eofChars)
    internal var parameterValueChars = setOf(':', '}', '$')
    internal var expressionValueChars = setOf('#', ']')

    init {
        charProcessors.add(LexerWhitespaceProcessor())
        charProcessors.add(LexerBracketProcessor())
        charProcessors.add(LexerTextAndStringProcessor())
        charProcessors.add(LexerCommentProcessor())
        charProcessors.add(LexerDotProcessor())
        charProcessors.add(LexerEqualProcessor())
        charProcessors.add(LexerParameterProcessor())
        charProcessors.add(LexerObjectProcessor())
        charProcessors.add(LexerExpressionProcessor())
        charProcessors.add(LexerArrayProcessor())
        charProcessors.add(LexerValueProcessor())
    }

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

    override fun advance() {
        if (myPosition >= myEndOffset) {
            myCurrentToken = null
            return
        }
        myCurrentTokenStart = myPosition
        val c = myBuffer[myPosition]

        charProcessors.any { it.process(c, this) }
    }

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

    internal fun isStringAtPosition(keyword: String): Boolean {
        val length = keyword.length
        return myEndOffset - myPosition >= length && myBuffer.subSequence(myPosition, myPosition + length).toString() == keyword
    }

    internal fun isStringAtPosition(): Boolean {
        return (myBuffer[myPosition] == '\'' || myBuffer[myPosition] == '"') && is_previous_not_backslash()
    }

    internal fun is_previous_not_backslash() = (myPosition > 0 && myBuffer.length > 1 && myBuffer[myPosition - 1] != '\\')

    internal fun advanceToNextSpecialChar(specialChars: Set<Char>, nextState: Int = STATE_DEFAULT, shouldReturnWhitespace: Boolean = true) {
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

    override fun getBufferSequence(): CharSequence {
        return myBuffer
    }

    override fun getBufferEnd(): Int {
        return myEndOffset
    }

}