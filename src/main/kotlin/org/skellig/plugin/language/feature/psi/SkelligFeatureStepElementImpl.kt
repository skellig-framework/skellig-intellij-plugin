package org.skellig.plugin.language.feature.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

abstract class SkelligFeatureStepElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), SkelligFeatureStepElement {

    override fun getValue(): Any? {
        return node.text
    }

    override fun getNameIdentifier(): PsiElement? {
        return node.psi
    }

    override fun setName(name: String): PsiElement {
        /*val keyNode: ASTNode? = this.node.findChildByType(SkelligTestDataTypes.KEY)
        if (keyNode != null) {
            val property: SimpleProperty? = SkelligTestDataElementFactory.createProperty(this.project, name)
            val newKeyNode: ASTNode = property?.getFirstChild()?.getNode()!!
            this.node.replaceChild(keyNode, newKeyNode)
        }*/
        return this
    }

    override fun getName(): String {
        val n = this.node.psi.children
                .filter { c -> c.elementType == SkelligFeatureTypes.TEXT_OR_STRING }
                .joinToString(" ") { i -> i.text }
        return n
    }
}