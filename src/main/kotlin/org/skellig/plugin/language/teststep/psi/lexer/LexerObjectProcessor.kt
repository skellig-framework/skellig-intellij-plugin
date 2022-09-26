package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_DEFAULT
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerObjectProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if ((c == '{' || c == '}') && lexer.myState != STATE_INSIDE_STRING) {
            lexer.myCurrentToken = if (c == '{') SkelligTestStepTokenTypes.OBJECT_OPEN_BRACKET else SkelligTestStepTokenTypes.OBJECT_CLOSE_BRACKET
            if (lexer.state == ARRAY_STATE) {
                lexer.myState = STATE_DEFAULT
            }
            lexer.myPosition++
            return true
        }
        return false
    }
}