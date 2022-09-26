package org.skellig.plugin.language.teststep.psi.lexer

import org.skellig.plugin.language.teststep.psi.SkelligTestStepTokenTypes
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerCommentProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (isCommentAtPosition(lexer) && lexer.myState != STATE_INSIDE_STRING) {
            lexer.myCurrentToken = SkelligTestStepTokenTypes.COMMENT
            lexer.advanceToNextSpecialChar(lexer.eofChars)
            return true
        }
        return false
    }

    private fun isCommentAtPosition(lexer: SkelligTestStepLexer): Boolean {
        return lexer.myBuffer.length > lexer.myPosition + 1 && lexer.myBuffer[lexer.myPosition] == '/' && lexer.myBuffer[lexer.myPosition + 1] == '/'
    }
}