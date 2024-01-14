package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.skellig.plugin.language.teststep.psi.SkelligTestStepLanguage
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepFormattingModelBuilder : FormattingModelBuilder {

    /*override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val file: PsiFile = formattingContext.containingFile
        val fileElement = TreeUtil.getFileElement(
            (SourceTreeToPsiMap.psiElementToTree(formattingContext.psiElement) as TreeElement)
        )
        val rootBlock = SkelligTestStepBlock(fileElement)
        return DocumentBasedFormattingModel(
            rootBlock, file.project, formattingContext.codeStyleSettings, file.fileType,
            file
        )
    }*/

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(settings, SkelligTestStepLanguage.INSTANCE)
            .around(SkelligTestStepTypes.VALUE_ASSIGN)
            .spaceIf(settings.getCommonSettings(SkelligTestStepLanguage.INSTANCE.id).SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .before(SkelligTestStepTypes.VALUE)
            .none()
    }

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        return FormattingModelProvider
            .createFormattingModelForPsiFile(
                formattingContext.containingFile,
                SkelligTestStepBlock(formattingContext.node),
//                SkelligTestStepBlock(
//                    formattingContext.node,
//                    Wrap.createWrap(WrapType.NONE, false),
//                    Alignment.createAlignment(),
//                    Indent.getNoneIndent(),
//                    createSpaceBuilder(codeStyleSettings)
//                ),
                codeStyleSettings
            )
    }
}