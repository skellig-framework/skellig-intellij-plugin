// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import org.jetbrains.plugins.cucumber.psi.SkelligFileType

object CucumberElementFactory {
    fun createTempPsiFile(project: Project?, text: String): PsiElement {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "temp." + SkelligFileType.INSTANCE.defaultExtension,
            SkelligFileType.INSTANCE,
            text, LocalTimeCounter.currentTime(), true
        )
    }
}