package org.skellig.plugin.language.testdata.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes


class SimpleFormattingModelBuilder : FormattingModelBuilder {

    @NotNull
    override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
        return FormattingModelProvider
                .createFormattingModelForPsiFile(element.containingFile,
                        SkelligTestDataBlock(element.node,
                                Wrap.createWrap(WrapType.NONE, false),
                                Alignment.createAlignment(),
                                createSpaceBuilder(settings)), settings)
    }

    @Nullable
    override fun getRangeAffectingIndent(file: PsiFile?, offset: Int, elementAtOffset: ASTNode?): TextRange? {
        return null
    }

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder? {
        return SpacingBuilder(settings, SkelligTestDataLanguage.INSTANCE)
                .around(SkelligTestDataTypes.SEPARATOR)
                .spaceIf(settings.getCommonSettings(SkelligTestDataLanguage.INSTANCE.id).SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(SkelligTestDataTypes.SYMBOLS)
                .none()
    }
}