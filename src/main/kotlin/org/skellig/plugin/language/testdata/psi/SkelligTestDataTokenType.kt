package org.skellig.plugin.language.testdata.psi

import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage

class SkelligTestDataTokenType(debugName: String) : IElementType(debugName, SkelligTestDataLanguage.INSTANCE) {

    override fun toString(): String {
        return "SkelligTestData." + super.toString()
    }
}