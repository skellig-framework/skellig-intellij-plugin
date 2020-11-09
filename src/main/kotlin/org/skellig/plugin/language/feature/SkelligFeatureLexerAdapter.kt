package org.skellig.plugin.language.feature

import com.intellij.lexer.FlexAdapter

class SkelligFeatureLexerAdapter : FlexAdapter(SkelligFeatureLexer(null)) {

}