package org.skellig.plugin.language.teststep.psi

import com.intellij.psi.tree.IElementType

class SkelligTestStepTokenType(debugName: String?) : IElementType(debugName!!, SkelligTestStepLanguage.INSTANCE) {

    override fun toString(): String {
        return "SkelligTestStepTokenType." + super.toString()
    }
}