// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi

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
import org.jetbrains.plugins.cucumber.psi.impl.GherkinFileImpl
import org.jetbrains.plugins.skellig.teststep.psi.impl.*

class SkelligTestStepParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        return SkelligTestStepLexer(PlainSkelligTestStepKeywordProvider())
    }

    override fun createParser(project: Project): PsiParser {
        return SkelligTestStepParser()
    }

    override fun getFileNodeType(): IFileElementType = SkelligTestStepElementTypes.FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        if (node.elementType === SkelligTestStepElementTypes.STEP) return SkelligTestStepImpl(node)
        return if (node.elementType === SkelligTestStepElementTypes.VARIABLES) SkelligTestStepVariablesImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.REQUEST) SkelligTestStepRequestImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.VALIDATION) SkelligTestStepValidationImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.KEYWORD) SkelligTestStepKeywordImpl(node)

        else if (node.elementType === SkelligTestStepElementTypes.EXPRESSION) SkelligTestStepExpressionImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.PARAMETER) SkelligTestStepParameterImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.FUNCTION) SkelligTestStepFunctionImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.TEXT) SkelligTestStepTextImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.PROPERTY) SkelligTestStepPropertyImpl(node)

        else if (node.elementType === SkelligTestStepElementTypes.VALUE) SkelligTestStepSimpleValueImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.OBJECT) SkelligTestStepObjectImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.ARRAY) SkelligTestStepArrayImpl(node)
        else if (node.elementType === SkelligTestStepElementTypes.FIELD_VALUE) SkelligTestStepFieldValuePairImpl(node)
        else PsiUtilCore.NULL_PSI_ELEMENT
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return SkelligTestStepFileImpl(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        // Line break between line comment and other elements
        val leftElementType: IElementType = left.elementType
        if (leftElementType === SkelligTestStepTokenTypes.COMMENT) {
            return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        }
        return if (right.elementType === SkelligTestStepTokenTypes.PARAMETER ||
            right.elementType === SkelligTestStepTokenTypes.EXPRESSION ||
            right.elementType === SkelligTestStepTokenTypes.FUNCTION) {
            ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        } else ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        private val WHITESPACE: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
        private val COMMENTS: TokenSet = TokenSet.create(SkelligTestStepTokenTypes.COMMENT)
    }
}