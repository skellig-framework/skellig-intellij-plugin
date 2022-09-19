package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang.StringUtils
import org.jetbrains.plugins.cucumber.psi.*
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementTypes
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepTokenTypes
import java.util.regex.Pattern

interface SkelligTestStepParameter : PsiElement {

}

class SkelligTestStepParameterImpl(node: ASTNode) : SkelligTestStepPsiElementBase(node), SkelligTestStepParameter {

    override fun getReference(): PsiReference {
        return SkelligTestStepParameterReference(this)
    }

    override fun getName(): String {
        return text
    }

    override fun getText(): String {
        return this.node.findChildByType(SkelligTestStepTokenTypes.PARAMETER)?.text ?: node.text
    }

    override fun getUseScope(): SearchScope {
        return LocalSearchScope(containingFile)
    }

    override fun toString(): String {
        return "TestStepParameter:$text"
    }

    override fun acceptTestStep(visitor: SkelligTestStepElementVisitor) {
        visitor.visit(this)
    }

}

class SkelligTestStepParameterReference(stepParameter: SkelligTestStepParameter) : SkelligTestStepSimpleReference(stepParameter) {
    val element: SkelligTestStepParameter
        get() = super.getElement() as SkelligTestStepParameter

    override fun resolve(): PsiElement? {
        val parameterText = element.text
        if (StringUtils.isNumeric(parameterText)) {
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStep::class.java) ?: return null
            val groupPattern = Pattern.compile("\\(.*\\)")
            val matcher = groupPattern.matcher(testStep.stepName)
            if (matcher.find()) {
                return testStep
            }
        } else {
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStep::class.java) ?: return null
            val variables = PsiTreeUtil.getChildOfType(testStep, SkelligTestStepVariables::class.java) ?: return null
            for (node in variables.children) {
                if (node is SkelligTestStepFieldValuePair) {
                    val text = node.getText()
                    if (text.startsWith(parameterText)) {
                        return node
                    }
                }
            }
        }
        return null
    }
}