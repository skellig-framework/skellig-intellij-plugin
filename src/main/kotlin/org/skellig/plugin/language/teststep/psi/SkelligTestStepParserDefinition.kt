package org.skellig.plugin.language.teststep.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.SkelligTestStepParser

class SkelligTestStepParserDefinition : ParserDefinition {
    private val file = IFileElementType(SkelligTestStepLanguage.INSTANCE)

    override fun createLexer(project: Project?): Lexer = SkelligTestStepLexerAdapter()

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project?): PsiParser = SkelligTestStepParser()

    override fun getFileNodeType(): IFileElementType = file

    override fun createFile(viewProvider: FileViewProvider): PsiFile = SkelligTestStepFile(viewProvider)

    override fun createElement(node: ASTNode?): PsiElement = SkelligTestStepTypes.Factory.createElement(node)

    override fun spaceExistenceTypeBetweenTokens(
        left: ASTNode,
        right: ASTNode,
    ): ParserDefinition.SpaceRequirements {
        // Line break between line comment and other elements
        val leftElementType: IElementType = left.elementType
        if (leftElementType === SkelligTestStepTypes.COMMENT) {
            return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        }
        return if (right.elementType === SkelligTestStepTypes.VALUE_ASSIGN || left.elementType === SkelligTestStepTypes.VALUE_ASSIGN) {
            ParserDefinition.SpaceRequirements.MUST
        } else {
            ParserDefinition.SpaceRequirements.MAY
        }
    }
}

private val COMMENTS: TokenSet = TokenSet.create(SkelligTestStepTypes.COMMENT)
