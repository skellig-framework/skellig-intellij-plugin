package org.skellig.plugin.language.teststep.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.SkelligTestStepParser

class SkelligTestStepParserDefinition : ParserDefinition {

    companion object {
        private val WHITESPACE: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
//        private val COMMENTS: TokenSet = TokenSet.create(SkelligTestStepTokenTypes.COMMENT)
    }

    private val FILE = IFileElementType(SkelligTestStepLanguage.INSTANCE)

    override fun createLexer(project: Project?): Lexer {
        return SkelligTestStepLexerAdapter()
    }

    override fun getCommentTokens(): TokenSet {
//        return COMMENTS
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createParser(project: Project?): PsiParser {
        return SkelligTestStepParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return SkelligTestStepFile(viewProvider)
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return SkelligTestStepTypes.Factory.createElement(node)
    }

    /*override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        // Line break between line comment and other elements
        val leftElementType: IElementType = left.elementType
        if (leftElementType === SkelligTestStepTokenTypes.COMMENT) {
            return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        }
        return if (right.elementType === SkelligTestStepTokenTypes.PARAMETER ||
            right.elementType === SkelligTestStepTokenTypes.EXPRESSION ||
            right.elementType === SkelligTestStepTokenTypes.FUNCTION
        ) {
            ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        } else ParserDefinition.SpaceRequirements.MAY
    }*/

}