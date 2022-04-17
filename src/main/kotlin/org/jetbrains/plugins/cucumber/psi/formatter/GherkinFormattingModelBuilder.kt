// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.formatter

import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.DocumentBasedFormattingModel
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.impl.source.tree.TreeUtil

class GherkinFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(element: PsiElement, settings: CodeStyleSettings?): FormattingModel {
        val file: PsiFile = element.containingFile
        val fileElement = TreeUtil.getFileElement(
            (SourceTreeToPsiMap.psiElementToTree(element) as TreeElement)
        )
        val rootBlock = SkelligBlock(fileElement)
        //FormattingModelDumper.dumpFormattingModel(rootBlock, 0, System.out);
        return DocumentBasedFormattingModel(
            rootBlock, file.project, settings, file.fileType,
            file
        )
    }
}