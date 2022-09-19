package org.skellig.plugin.language.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepElementVisitor

interface SkelligTestStepProperty : PsiElement

class SkelligTestStepPropertyImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepProperty {

     // TODO: property can have similar children as Simple Value element
    /*companion object {
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


    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                referencesInner, this
            )
        }
    }

    private val referencesInner: Array<PsiReference>
        private get() = ReferenceProvidersRegistry.getReferencesFromProviders(this)*/


    override fun getName(): String {
        return elementText
    }

    override fun getPresentableText(): String {
        return "SkelligTestStepProperty: $name"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}