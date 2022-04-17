// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi

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
import org.jetbrains.plugins.cucumber.psi.impl.*

class SkelligParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        return SkelligLexer(PlainGherkinKeywordProvider())
    }

    override fun createParser(project: Project): PsiParser {
        return SkelligParser()
    }

    override fun getFileNodeType(): IFileElementType =  GherkinElementTypes.GHERKIN_FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        if (node.elementType === GherkinElementTypes.FEATURE) return GherkinFeatureImpl(node)
        if (node.elementType === GherkinElementTypes.FEATURE_HEADER) return GherkinFeatureHeaderImpl(node)
        if (node.elementType === GherkinElementTypes.SCENARIO) return GherkinScenarioImpl(node)
        if (node.elementType === GherkinElementTypes.STEP) return GherkinStepImpl(node)
        if (node.elementType === GherkinElementTypes.SCENARIO_OUTLINE) return GherkinScenarioOutlineImpl(node)
        if (node.elementType === GherkinElementTypes.RULE) return GherkinRuleImpl(node)
        if (node.elementType === GherkinElementTypes.EXAMPLES_BLOCK) return GherkinExamplesBlockImpl(node)
        if (node.elementType === GherkinElementTypes.TABLE) return GherkinTableImpl(node)
        if (node.elementType === GherkinElementTypes.TABLE_ROW) return GherkinTableRowImpl(node)
        if (node.elementType === GherkinElementTypes.TABLE_CELL) return GherkinTableCellImpl(node)
        if (node.elementType === GherkinElementTypes.TABLE_HEADER_ROW) return GherkinTableHeaderRowImpl(node)
        if (node.elementType === GherkinElementTypes.TAG) return GherkinTagImpl(node)
        if (node.elementType === GherkinElementTypes.STEP_PARAMETER) return GherkinStepParameterImpl(node)
        return if (node.elementType === GherkinElementTypes.PYSTRING) GherkinPystringImpl(node) else PsiUtilCore.NULL_PSI_ELEMENT
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return GherkinFileImpl(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        // Line break between line comment and other elements
        val leftElementType: IElementType = left.elementType
        if (leftElementType === GherkinTokenTypes.COMMENT) {
            return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        }
        return if (right.elementType === GherkinTokenTypes.EXAMPLES_KEYWORD) {
            ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        } else ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        private val WHITESPACE: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
        private val COMMENTS: TokenSet = TokenSet.create(GherkinTokenTypes.COMMENT)
    }
}