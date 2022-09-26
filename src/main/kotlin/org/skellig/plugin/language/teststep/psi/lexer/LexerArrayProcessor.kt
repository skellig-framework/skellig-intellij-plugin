package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_DEFAULT
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerArrayProcessor : LexerCharProcessor {
        override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
            if ((c == '[' || c == ']') && lexer.myState != STATE_INSIDE_STRING) {
                lexer.myCurrentToken = if (c == '[') {
                    lexer.arraysCount++
                    lexer.myState = ARRAY_STATE
                    SkelligTestStepTokenTypes.ARRAY_OPEN_BRACKET
                } else {
                    lexer.arraysCount--
                    lexer.myState = STATE_DEFAULT
                    SkelligTestStepTokenTypes.ARRAY_CLOSE_BRACKET
                }
                lexer.myPosition++
                return true
            }
            return false
        }
    }