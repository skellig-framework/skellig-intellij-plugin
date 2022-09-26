package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerDotProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (c == '.' && lexer.myState != STATE_INSIDE_STRING) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.DOT
            lexer.myPosition++
            return true
        }
        return false
    }
}