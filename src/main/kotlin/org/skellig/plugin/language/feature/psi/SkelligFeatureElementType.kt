package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.feature.SkelligFeatureLanguage

class SkelligFeatureElementType(debugName: String) : IElementType(debugName, SkelligFeatureLanguage.INSTANCE) {
}