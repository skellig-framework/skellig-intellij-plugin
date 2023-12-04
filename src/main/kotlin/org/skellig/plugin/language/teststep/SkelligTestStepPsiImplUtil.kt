// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.skellig.plugin.language.teststep

import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName

object SkelligTestStepPsiImplUtil {
    fun getName(element: SkelligTestStepTestStepName): String {
        return element.text
    }

    fun setName(element: SkelligTestStepTestStepName, newName: String?): PsiElement {
        return element
    }
}
