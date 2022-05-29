// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class SkelligTestStepElementType(@NonNls val debugName: String) : IElementType(debugName, SkelligTestStepLanguage.INSTANCE)