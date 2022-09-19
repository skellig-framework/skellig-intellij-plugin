package org.skellig.plugin.language.feature.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.feature.psi.GherkinElementVisitor
import org.skellig.plugin.language.feature.psi.GherkinPsiElement
import org.skellig.plugin.language.feature.psi.GherkinTokenTypes
import javax.swing.Icon

abstract class GherkinPsiElementBase(node: ASTNode) : ASTWrapperPsiElement(node), GherkinPsiElement {
    companion object {
        private val TEXT_FILTER: TokenSet = TokenSet.create(GherkinTokenTypes.Companion.TEXT)
    }

    open val elementText: String
        get() {
            val node: ASTNode = getNode()
            val children = node.getChildren(TEXT_FILTER)
            return StringUtil.join(children, { astNode: ASTNode -> astNode.text }, " ").trim { it <= ' ' }
        }

    override fun getPresentation(): ItemPresentation? {
       return object : ItemPresentation {

            override fun getPresentableText(): String? {
                return toString()
            }

            override fun getLocationString(): String? {
                return toString()
            }

            override fun getIcon(open: Boolean): Icon? {
                return getIcon(Iconable.ICON_FLAG_VISIBILITY)
            }
        }
    }

    protected open fun getPresentableText(): String? {
        return toString()
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
        if (visitor is GherkinElementVisitor) {
            acceptGherkin(visitor as GherkinElementVisitor)
        } else {
            super.accept(visitor)
        }
    }

    protected abstract fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor)


}