package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.SIMPLE_VALUE_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerEqualProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (c == '=' && lexer.myState != STATE_INSIDE_STRING) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.EQUAL
            lexer.myState = SIMPLE_VALUE_STATE
            lexer.myPosition++
            return true
        }
        return false
    }
}