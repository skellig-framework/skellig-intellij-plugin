package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.skellig.plugin.language.feature.psi.SkelligElementVisitor
import org.skellig.plugin.language.feature.psi.SkelligPystring

class SkelligPystringImpl(
    node: ASTNode,
) : SkelligPsiElementBase(node),
    SkelligPystring {
    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitPystring(this)
    }

    override fun toString(): String = "SkelligPystring"

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val docStringSep = firstChild.text
        val startOffset = if (text.startsWith(docStringSep)) docStringSep.length else 0
        val endOffset = if (text.endsWith(docStringSep)) docStringSep.length else 0
        (firstChild.nextSibling as LeafPsiElement).replaceWithText(text.substring(startOffset, text.length - endOffset))
        firstChild.nextSibling.nextSibling.replace(lastChild)
        return this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> = LiteralTextEscaper.createSimple(this)
}
