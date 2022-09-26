package org.skellig.plugin.language.feature.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiUtilCore
import org.skellig.plugin.language.feature.psi.impl.*

class SkelligParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        return SkelligLexer(PlainSkelligKeywordProvider())
    }

    override fun createParser(project: Project): PsiParser {
        return SkelligParser()
    }

    override fun getFileNodeType(): IFileElementType =  SkelligElementTypes.SKELLIG_FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        if (node.elementType === SkelligElementTypes.FEATURE) return SkelligFeatureImpl(node)
        if (node.elementType === SkelligElementTypes.FEATURE_HEADER) return SkelligFeatureHeaderImpl(node)
        if (node.elementType === SkelligElementTypes.STEP) return SkelligFeatureStepImpl(node)
        if (node.elementType === SkelligElementTypes.SCENARIO) return SkelligScenarioOutlineImpl(node)
        if (node.elementType === SkelligElementTypes.RULE) return SkelligRuleImpl(node)
        if (node.elementType === SkelligElementTypes.EXAMPLES_BLOCK) return SkelligExamplesBlockImpl(node)
        if (node.elementType === SkelligElementTypes.TABLE) return SkelligTableImpl(node)
        if (node.elementType === SkelligElementTypes.TABLE_ROW) return SkelligTableRowImpl(node)
        if (node.elementType === SkelligElementTypes.TABLE_CELL) return SkelligTableCellImpl(node)
        if (node.elementType === SkelligElementTypes.TABLE_HEADER_ROW) return SkelligTableHeaderRowImpl(node)
        if (node.elementType === SkelligElementTypes.TAG) return SkelligTagImpl(node)
        if (node.elementType === SkelligElementTypes.STEP_PARAMETER) return SkelligStepParameterImpl(node)
        return if (node.elementType === SkelligElementTypes.PYSTRING) SkelligPystringImpl(node) else PsiUtilCore.NULL_PSI_ELEMENT
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return SkelligFileImpl(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        // Line break between line comment and other elements
        val leftElementType: IElementType = left.elementType
        if (leftElementType === SkelligTokenTypes.COMMENT) {
            return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        }
        return if (right.elementType === SkelligTokenTypes.EXAMPLES_KEYWORD) {
            ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        } else ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        private val WHITESPACE: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
        private val COMMENTS: TokenSet = TokenSet.create(SkelligTokenTypes.COMMENT)
    }
}