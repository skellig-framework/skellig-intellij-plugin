package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.SIMPLE_VALUE_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_PARAMETER
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_PARAMETER_DEFAULT

class LexerParameterProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (isParameterAtPosition(lexer)) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_OPEN_BRACKET
            lexer.myPosition += 2
            lexer.myState = STATE_PARAMETER
            lexer.currentParametersCount++
            return true
        } else if (lexer.myState == STATE_PARAMETER_DEFAULT) {
            when (c) {
                '}' -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_CLOSE_BRACKET
                    lexer.myPosition++
                    lexer.currentParametersCount--
                    lexer.myState = if (lexer.currentParametersCount == 0) SIMPLE_VALUE_STATE else STATE_PARAMETER
                }
                else -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.TEXT
                    lexer.advanceToNextSpecialChar(lexer.specialChars, STATE_PARAMETER_DEFAULT)
                }
            }
            return true
        }
        // Process parameter
        else if (lexer.currentParametersCount > 0 || lexer.myState == STATE_PARAMETER) {
            // examples:
            // ${key}
            // ${key:}
            // ${key: default}
            // ${key: ${key_2}}
            when (c) {
                '}' -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.PARAMETER_CLOSE_BRACKET
                    lexer.myPosition++
                    lexer.currentParametersCount--
                    lexer.myState = if (lexer.currentParametersCount == 0)
                        if (lexer.arraysCount > 0) ARRAY_STATE else SIMPLE_VALUE_STATE
                    else STATE_PARAMETER
                }
                ':' -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.COLON
                    lexer.myState = STATE_PARAMETER_DEFAULT
                    lexer.myPosition++
                }
                else -> {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.PARAMETER
                    lexer.advanceToNextSpecialChar(lexer.parameterValueChars, STATE_PARAMETER)
                }
            }
            return true
        }
        return false
    }

    private fun isParameterAtPosition(lexer: SkelligTestStepLexer): Boolean {
        return lexer.is_previous_not_backslash() &&
                lexer.myBuffer.length > lexer.myPosition + 1 && lexer.myBuffer[lexer.myPosition] == '$' && lexer.myBuffer[lexer.myPosition + 1] == '{'
    }

}