package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import org.jetbrains.plugins.cucumber.CucumberUtil
import org.jetbrains.plugins.cucumber.psi.*
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementTypes
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepTokenTypes

interface SkelligTestStepSimpleValue : PsiElement {

    val text: SkelligTestStepText?
    val function: SkelligTestStepText?
    val parameter: SkelligTestStepParameter?
    val expression: SkelligTestStepExpression?
}

class SkelligTestStepSimpleValueImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepSimpleValue {

    companion object {
        private val TEXT_FILTER = TokenSet
            .create(
                SkelligTestStepTokenTypes.TEXT,
                SkelligTestStepTokenTypes.STRING_TEXT,
                SkelligTestStepTokenTypes.PARAMETER,
                SkelligTestStepTokenTypes.FUNCTION,
                SkelligTestStepTokenTypes.EXPRESSION,
                SkelligTestStepTokenTypes.DOT,
                TokenType.WHITE_SPACE,
            )
    }

    override fun toString(): String {
        return "SkelligTestStepSimpleValue: $name"
    }

    override val elementText: String
        get() {
            val node = node
            val children = node.getChildren(TEXT_FILTER)
            return StringUtil.join(children, { astNode: ASTNode -> astNode.text }, "").trim { it <= ' ' }
        }

    override val text: SkelligTestStepText?
        get() {
            val tableNode: ASTNode? = node.findChildByType(SkelligTestStepElementTypes.TEXT)
            return if (tableNode == null) null else tableNode.psi as SkelligTestStepText
        }

    override val function: SkelligTestStepText?
        get() {
            val tableNode: ASTNode? = node.findChildByType(SkelligTestStepElementTypes.FUNCTION)
            return if (tableNode == null) null else tableNode.psi as SkelligTestStepText
        }

    override val parameter: SkelligTestStepParameter?
        get() {
            val tableNode: ASTNode? = node.findChildByType(SkelligTestStepElementTypes.PARAMETER)
            return if (tableNode == null) null else tableNode.psi as SkelligTestStepParameter
        }

    override val expression: SkelligTestStepExpression?
        get() {
            val tableNode: ASTNode? = node.findChildByType(SkelligTestStepElementTypes.EXPRESSION)
            return if (tableNode == null) null else tableNode.psi as SkelligTestStepExpression
        }

    override fun getPresentableText(): String {
        return name
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                referencesInner, this
            )
        }
    }

    private val referencesInner: Array<PsiReference>
        private get() = ReferenceProvidersRegistry.getReferencesFromProviders(this)

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

    override fun getName(): String {
        return elementText
    }
}