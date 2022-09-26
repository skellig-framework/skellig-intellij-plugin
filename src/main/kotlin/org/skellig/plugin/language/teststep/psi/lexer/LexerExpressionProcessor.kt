package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.SIMPLE_VALUE_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_EXPRESSION

class LexerExpressionProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (isExpressionAtPosition(lexer)) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION_OPEN_BRACKET
            lexer.myPosition += 2
            lexer.myState = STATE_EXPRESSION
            lexer.currentExpressionsCount++
            return true
        } else if (lexer.currentExpressionsCount > 0 || lexer.myState == STATE_EXPRESSION) {
            // example: #[a.b.subString(1,2)]
            when (c) {
                ']' -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION_CLOSE_BRACKET
                    lexer.myPosition++
                    lexer.currentExpressionsCount--
                    lexer.myState = if (lexer.currentExpressionsCount == 0)
                        if (lexer.arraysCount > 0) ARRAY_STATE else SIMPLE_VALUE_STATE
                    else STATE_EXPRESSION
                }
                else -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.EXPRESSION
                    lexer.advanceToNextSpecialChar(lexer.expressionValueChars.union(lexer.parameterValueChars), STATE_EXPRESSION)
                }
            }
            return true
        }
        return false
    }

    private fun isExpressionAtPosition(lexer: SkelligTestStepLexer): Boolean {
        return lexer.is_previous_not_backslash() &&
                lexer.myBuffer.length > lexer.myPosition + 1 && lexer.myBuffer[lexer.myPosition] == '#' && lexer.myBuffer[lexer.myPosition + 1] == '['
    }
}