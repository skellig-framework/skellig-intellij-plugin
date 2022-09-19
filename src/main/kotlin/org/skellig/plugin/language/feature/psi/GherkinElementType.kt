package org.skellig.plugin.language.feature.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class GherkinElementType(@NonNls debugName: String) : IElementType(debugName, SkelligLanguage.INSTANCE)