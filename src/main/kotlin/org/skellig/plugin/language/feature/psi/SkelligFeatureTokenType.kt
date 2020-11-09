package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.feature.SkelligFeatureLanguage

class SkelligFeatureTokenType(debugName: String) : IElementType(debugName, SkelligFeatureLanguage.INSTANCE) {

    override fun toString(): String {
        return "SkelligFeature." + super.toString()
    }
}