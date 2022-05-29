// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepFile
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepFileType
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepLanguage

class SkelligTestStepFileImpl(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SkelligTestStepLanguage.INSTANCE), SkelligTestStepFile {

    override fun getFileType(): FileType = SkelligTestStepFileType.INSTANCE


    override fun getTestSteps(): Array<SkelligTestStep?> = findChildrenByClass(SkelligTestStep::class.java)

    override fun toString(): String {
        return "SkelligTestStepsFile:$name"
    }

    override fun findElementAt(offset: Int): PsiElement? {
        var result: PsiElement? = super.findElementAt(offset)
        if (result == null && offset == getTextLength()) {
            val last: PsiElement? = getLastChild()
            result = if (last != null) last.getLastChild() else last
        }
        return result
    }
}