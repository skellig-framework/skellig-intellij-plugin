package org.skellig.plugin.language.feature

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.feature.psi.SkelligFeatureFile
import org.skellig.plugin.language.feature.psi.SkelligFeatureTypes
import org.skellig.plugin.parser.feature.SkelligFeatureParser


class SkelligFeatureParserDefinition : ParserDefinition {

    val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    val COMMENTS = TokenSet.create(SkelligFeatureTypes.COMMENT)
    val FILE = IFileElementType(SkelligFeatureLanguage.INSTANCE)

    override fun createLexer(project: Project?): Lexer {
        return SkelligFeatureLexerAdapter()
    }

    override fun getWhitespaceTokens(): TokenSet {
        return WHITE_SPACES
    }

    override fun getCommentTokens(): TokenSet {
        return COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createParser(project: Project?): PsiParser {
        return SkelligFeatureParser()
    }

    override fun getFileNodeType(): IFileElementType? {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider?): PsiFile? {
        return SkelligFeatureFile(viewProvider!!)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): SpaceRequirements? {
        return SpaceRequirements.MAY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return SkelligFeatureTypes.Factory.createElement(node)
    }
}