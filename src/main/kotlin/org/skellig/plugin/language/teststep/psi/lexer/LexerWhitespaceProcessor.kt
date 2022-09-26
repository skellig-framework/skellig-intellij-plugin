package org.skellig.plugin.language.teststep.psi.lexer

import com.intellij.psi.TokenType
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.ARRAY_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.SIMPLE_VALUE_STATE
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_DEFAULT
import org.skellig.plugin.language.teststep.psi.lexer.SkelligTestStepLexer.Companion.STATE_INSIDE_STRING

class LexerWhitespaceProcessor : LexerCharProcessor {
    override fun process(c: Char, lexer: SkelligTestStepLexer): Boolean {
        if (lexer.myState == SIMPLE_VALUE_STATE && isNewLineChar(c, lexer)) {
            advanceOverWhitespace(lexer)
            lexer.myCurrentToken = TokenType.NEW_LINE_INDENT
            while (lexer.myPosition < lexer.myEndOffset && isNewLineChar(lexer.myBuffer[lexer.myPosition], lexer)) {
                advanceOverWhitespace(lexer)
            }
            return true
        } else if (lexer.myState != STATE_INSIDE_STRING && Character.isWhitespace(c)) {
            advanceOverWhitespace(lexer, if (lexer.myState == ARRAY_STATE) lexer.myState else STATE_DEFAULT)
            lexer.myCurrentToken = TokenType.WHITE_SPACE
            while (lexer.myPosition < lexer.myEndOffset && Character.isSpaceChar(lexer.myBuffer[lexer.myPosition])) {
                advanceOverWhitespace(lexer, if (lexer.myState == ARRAY_STATE) lexer.myState else STATE_DEFAULT)
                if (lexer.myPosition < lexer.myEndOffset && isNewLineChar(lexer.myBuffer[lexer.myPosition], lexer)) {
                    lexer.myCurrentToken = TokenType.NEW_LINE_INDENT
                    break
                }
            }
            return true
        }
        return false
    }

    private fun advanceOverWhitespace(lexer: SkelligTestStepLexer, nextState: Int = STATE_DEFAULT) {
        if (lexer.myBuffer[lexer.myPosition] == '\n') {
            lexer.myState = nextState
        }
        lexer.myPosition++
    }

    private fun isNewLineChar(c: Char, lexer: SkelligTestStepLexer) = lexer.eofChars.contains(c)
}