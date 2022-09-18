package org.jetbrains.plugins.skellig.teststep.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.tree.TokenSet
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor
import org.jetbrains.plugins.cucumber.psi.GherkinPsiElement
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepElementVisitor
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepTokenTypes
import javax.swing.Icon

abstract class SkelligTestStepPsiElementBase(node: ASTNode) : ASTWrapperPsiElement(node), GherkinPsiElement {

    companion object {
        private val TEXT_FILTER: TokenSet = TokenSet.create(SkelligTestStepTokenTypes.STRING_TEXT, SkelligTestStepTokenTypes.TEXT)
    }

    open val elementText: String
        get() {

            val children = node.getChildren(TEXT_FILTER)
            return StringUtil.join(children, { astNode: ASTNode -> astNode.text }, " ").trim { it <= ' ' }
        }

    override fun getPresentation(): ItemPresentation? {
        val fileName = this.containingFile.name
        return object : ItemPresentation {

            override fun getPresentableText(): String? {
                return elementText
            }

            override fun getLocationString(): String? {
                return fileName
            }

            override fun getIcon(open: Boolean): Icon? {
                return getIcon(Iconable.ICON_FLAG_READ_STATUS)
            }
        }
    }

    protected open fun getPresentableText(): String? {
        return elementText
    }

    protected fun buildPresentableText(prefix: String?): String {
        val result = StringBuilder(prefix)
        val name = elementText
        if (!StringUtil.isEmpty(name)) {
            result.append(": ").append(name)
        }
        return result.toString()
    }

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is SkelligTestStepElementVisitor) {
            acceptTestStep(visitor)
        } else {
            super.accept(visitor)
        }
    }

    protected abstract fun acceptTestStep(visitor: SkelligTestStepElementVisitor)

}