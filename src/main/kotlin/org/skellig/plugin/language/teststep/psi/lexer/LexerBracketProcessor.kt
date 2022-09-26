package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.PROPERTY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_DEFAULT
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_TEST_STEP

class LexerBracketProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (c == '(' && (lexer.myState == STATE_DEFAULT || lexer.myState == PROPERTY_STATE || lexer.myState == STATE_TEST_STEP)) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.OPEN_BRACKET
            lexer.myPosition++
            return true
        } else if (c == ')' && (lexer.myState == STATE_DEFAULT || lexer.myState == PROPERTY_STATE || lexer.myState == STATE_TEST_STEP)) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.CLOSE_BRACKET
            lexer.myState = STATE_DEFAULT
            lexer.myPosition++
            return true
        }
        return false
    }
}