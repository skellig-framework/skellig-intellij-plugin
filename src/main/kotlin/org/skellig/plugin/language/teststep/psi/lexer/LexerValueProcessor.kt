package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.PROPERTY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.SIMPLE_VALUE_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_DEFAULT
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_TEST_STEP

class LexerValueProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (lexer.myState == SIMPLE_VALUE_STATE) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.TEXT
            lexer.advanceToNextSpecialChar(lexer.specialChars, SIMPLE_VALUE_STATE)
            return true
        } else {
            if (lexer.myState == STATE_DEFAULT) {
                if (lexer.myState == STATE_DEFAULT && lexer.isStringAtPosition(SkelligTestStepTokenTypes.NAME.toString().toLowerCase())) {
                    lexer.myCurrentToken = SkelligTestStepTokenTypes.NAME
                    lexer.myPosition += lexer.myCurrentToken.toString().length
                    lexer.myState = STATE_TEST_STEP
                    return true
                } else {
                    for (keyword in lexer.myKeywords!!) {
                        if (lexer.isStringAtPosition(keyword!!) && lexer.afterKeywordChars.contains(lexer.myBuffer[lexer.myPosition + keyword.length])) {
                            val length = keyword.length
                            lexer.myCurrentToken = lexer.myKeywordProvider.getTokenType(keyword)
                            lexer.myPosition += length
                            return true
                        }
                    }
                }
            }
            lexer.myCurrentToken = if (lexer.myState != ARRAY_STATE && isProperty(lexer)) {
                lexer.advanceToNextSpecialChar(lexer.specialChars, PROPERTY_STATE)
                SkelligTestStepTokenTypes.PROPERTY
            } else {
                lexer.advanceToNextSpecialChar(lexer.specialChars, if (lexer.myState == ARRAY_STATE) lexer.myState else STATE_DEFAULT)
                SkelligTestStepTokenTypes.TEXT
            }
            return true
        }
    }

    private fun isProperty(lexer: SkelligTestStepLexer): Boolean {
        var position = lexer.myPosition
        //TODO: Ignore propertyValueChars in quotes, params and expression
        while (position < lexer.myEndOffset - 1 && !lexer.propertyValueChars.contains(lexer.myBuffer[position])) {
            position++
        }
        return if (lexer.myBuffer[position] == '=') {
            var backPosition = position - 1
            while (backPosition >= 0 && lexer.eofChars.contains(lexer.myBuffer[backPosition])) {
                backPosition--
            }
            backPosition == position - 1
        } else {
            lexer.propertyValueChars.contains(lexer.myBuffer[position])
        }
    }
}