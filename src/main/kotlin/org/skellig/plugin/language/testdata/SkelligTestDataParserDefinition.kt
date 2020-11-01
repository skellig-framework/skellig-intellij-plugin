package org.skellig.plugin.language.testdata

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
import org.skellig.plugin.language.testdata.psi.SkelligTestDataFile
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes
import org.skellig.plugin.parser.testdata.SkelligTestDataParser


class SkelligTestDataParserDefinition : ParserDefinition {

    val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    val COMMENTS = TokenSet.create(SkelligTestDataTypes.COMMENT)
    val FILE = IFileElementType(SkelligTestDataLanguage.INSTANCE)

    override fun createLexer(project: Project?): Lexer {
        return SkelligTestDataLexerAdapter()
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
        return SkelligTestDataParser()
    }

    override fun getFileNodeType(): IFileElementType? {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider?): PsiFile? {
        return SkelligTestDataFile(viewProvider!!)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): SpaceRequirements? {
        return SpaceRequirements.MAY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return SkelligTestDataTypes.Factory.createElement(node)
    }
}