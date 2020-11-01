package org.skellig.plugin.language.testdata.psi

import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage

class SkelligTestDataElementType(debugName: String) : IElementType(debugName, SkelligTestDataLanguage.INSTANCE) {
}