package org.skellig.plugin.language.testdata.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.psi.*
import org.skellig.plugin.language.testdata.psi.SkelligTestDataElementFactory
import org.skellig.plugin.language.testdata.psi.SkelligTestDataNamedElement
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes

open class SkelligTestDataNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), SkelligTestDataNamedElement {
    override fun getNameIdentifier(): PsiElement? {
        return node.findChildByType(SkelligTestDataTypes.NAME)?.psi
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

    fun getKey(): String {
//        val keyNode: ASTNode = this.node.findChildByType(SkelligTestDataTypes.KEY)!!
//        return keyNode.text.replace("\\\\ ".toRegex(), " ")
        return ""
    }

    fun getValue(): String {
//        val valueNode: ASTNode = this.getNode().findChildByType(SkelligTestDataTypes.VALUE)!!
//        return valueNode.text
        return ""
    }
}