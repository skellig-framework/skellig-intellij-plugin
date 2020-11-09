package org.skellig.plugin.language.feature.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.feature.SkelligFeatureLanguage
import org.skellig.plugin.language.feature.psi.SkelligFeatureTypes


class SkelligFeatureFormattingModelBuilder : FormattingModelBuilder {

    @NotNull
    override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
        return FormattingModelProvider
                .createFormattingModelForPsiFile(element.containingFile,
                        SkelligFeatureBlock(element.node,
                                Wrap.createWrap(WrapType.NONE, false),
                                Alignment.createAlignment(),
                                createSpaceBuilder(settings)), settings)
    }

    @Nullable
    override fun getRangeAffectingIndent(file: PsiFile?, offset: Int, elementAtOffset: ASTNode?): TextRange? {
        return null
    }

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder? {
        return SpacingBuilder(settings, SkelligFeatureLanguage.INSTANCE)
                .around(SkelligFeatureTypes.COLON)
                .spaceIf(settings.getCommonSettings(SkelligFeatureLanguage.INSTANCE.id).SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(SkelligFeatureTypes.SYMBOLS)
                .none()
    }
}