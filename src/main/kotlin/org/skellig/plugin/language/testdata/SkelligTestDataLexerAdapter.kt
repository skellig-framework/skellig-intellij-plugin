package org.skellig.plugin.language.testdata

import com.intellij.lexer.FlexAdapter

class SkelligTestDataLexerAdapter : FlexAdapter(SkelligTestDataLexer(null)) {

}