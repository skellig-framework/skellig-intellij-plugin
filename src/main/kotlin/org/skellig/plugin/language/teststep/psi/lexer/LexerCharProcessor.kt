package org.skellig.plugin.language.teststep.psi.lexer

interface LexerCharProcessor {
    fun process(c: Char, lexer: SkelligTestStepLexer): Boolean
}