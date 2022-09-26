package org.skellig.plugin.language.teststep.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class SkelligTestStepElementType(@NonNls val debugName: String) : IElementType(debugName, SkelligTestStepLanguage.INSTANCE)