package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementTypes

open class SkelligTestStepIdImpl(node: ASTNode) : SkelligTestStepFieldValuePairImpl(node) {

    override val property: SkelligTestStepProperty?
        get() {
            return node.findChildByType(SkelligTestStepElementTypes.ID)?.psi as SkelligTestStepProperty?
        }

    override fun toString(): String {
        return "SkelligTestStepIdImpl: $elementText"
    }

}