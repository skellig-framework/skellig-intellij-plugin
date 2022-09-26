package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_PARAMETER
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_TEST_STEP

class LexerTextAndStringProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (lexer.myState == STATE_TEST_STEP) {
            if (lexer.isStringAtPosition()) {
                lexer.myCurrentToken = SkelligTestStepTokenTypes.STRING_TEXT
                lexer.advanceToNextSpecialChar(lexer.stringChars, lexer.myState, false)
                lexer.myPosition++
            } else {
                lexer.myCurrentToken = SkelligTestStepTokenTypes.TEXT
                lexer.advanceToNextSpecialChar(lexer.specialChars, lexer.myState)
            }
            return true
        } else if (lexer.myState != STATE_PARAMETER && lexer.isStringAtPosition()) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.STRING_TEXT
            lexer.advanceToNextSpecialChar(lexer.stringChars, lexer.myState, false)
            lexer.myPosition++
            return true
        }
        return false
    }
}