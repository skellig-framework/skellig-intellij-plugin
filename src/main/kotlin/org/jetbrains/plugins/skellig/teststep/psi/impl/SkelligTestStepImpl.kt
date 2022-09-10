package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiCheckedRenameElement
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepTokenTypes

interface SkelligTestStep : PsiCheckedRenameElement {

    val stepName: String
    val variables: SkelligTestStepVariables?
    val request: SkelligTestStepRequest?
    val validation: SkelligTestStepValidation?
}

class SkelligTestStepImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStep {

    override val stepName: String
        get() {
            val node = node
            val firstText: ASTNode? =
                node.findChildByType(SkelligTestStepTokenTypes.STRING_TEXT) ?: node.findChildByType(SkelligTestStepTokenTypes.TEXT)
            if (firstText != null) {
                return firstText.text
            }
            return elementText
        }

    override val variables: SkelligTestStepVariables?
        get() {
            return node.findChildByType(SkelligTestStepTokenTypes.VARIABLES)?.psi as SkelligTestStepVariables?
        }

    override val request: SkelligTestStepRequest?
        get() {
            return node.findChildByType(SkelligTestStepTokenTypes.REQUEST)?.psi as SkelligTestStepRequest?
        }

    override val validation: SkelligTestStepValidation?
        get() {
            return node.findChildByType(SkelligTestStepTokenTypes.VALIDATIONS)?.psi as SkelligTestStepValidation?
        }

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        val newStep: SkelligTestStep? = null //GherkinChangeUtil.createStep(getKeyword().getText() + " " + name, getProject());
        //        replace(newStep);
        return newStep!!
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

    override fun checkSetName(name: String) {
//        if (!isRenameAllowed(name)) {
//            throw new IncorrectOperationException(CucumberBundle.message("cucumber.refactor.rename.bad_symbols"));
//        }
    }

    override fun toString(): String {
        return "SkelligTestStep: $elementText"
    }
}