package org.skellig.plugin.language.teststep.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.skellig.plugin.language.teststep.SkelligTestStepPsiImplUtil

// Keep custom PSI behavior in source so headless Grammar-Kit generation does not
// need to resolve utility methods before the generated PSI types exist.
abstract class SkelligTestStepNameMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    SkelligTestStepTestStepName {
    override fun getName(): String = SkelligTestStepPsiImplUtil.getName(this)

    override fun setName(name: String): PsiElement = SkelligTestStepPsiImplUtil.setName(this, name)

    override fun getNameIdentifier(): PsiElement? = SkelligTestStepPsiImplUtil.getNameIdentifier(this)

    override fun getPresentation(): ItemPresentation = SkelligTestStepPsiImplUtil.getPresentation(this)

    override fun getReference(): PsiReference = SkelligTestStepPsiImplUtil.getReference(this)
}

abstract class SkelligTestStepDeclarationMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    SkelligTestStepTestStepNameExpression {
    override fun getPresentation(): ItemPresentation = SkelligTestStepPsiImplUtil.getPresentation(this)
}

abstract class SkelligTestStepKeyMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    SkelligTestStepKey {
    override fun getPresentation(): ItemPresentation = SkelligTestStepPsiImplUtil.getPresentation(this)
}

abstract class SkelligTestStepFunctionMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    SkelligTestStepFunctionExpression {
    override fun getReference(): PsiReference = SkelligTestStepPsiImplUtil.getReference(this)
}

abstract class SkelligTestStepReferenceMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    SkelligTestStepReferenceKey {
    override fun getReference(): PsiReference = SkelligTestStepPsiImplUtil.getReference(this)
}
