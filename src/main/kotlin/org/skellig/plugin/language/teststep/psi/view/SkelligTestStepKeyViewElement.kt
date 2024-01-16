package org.skellig.plugin.language.teststep.psi.view

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFile
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression

class SkelligTestStepKeyViewElement(private val myElement: NavigatablePsiElement) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): Any {
        return myElement
    }

    override fun navigate(requestFocus: Boolean) {
        myElement.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return myElement.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return myElement.canNavigateToSource()
    }

    override fun getAlphaSortKey(): String {
        val name = myElement.name
        return name ?: ""
    }

    override fun getPresentation(): ItemPresentation {
        val presentation = myElement.presentation
        return presentation ?: PresentationData()
    }

    override fun getChildren(): Array<TreeElement> {
        if (myElement is SkelligTestStepFile) {
            val treeElements = mutableListOf<TreeElement>()
            PsiTreeUtil.getChildrenOfTypeAsList(myElement, SkelligTestStepTestStepNameExpression::class.java)
                .forEach { _ ->
                    PsiTreeUtil.getChildrenOfTypeAsList(myElement, SkelligTestStepKey::class.java)
                        .forEach { treeElements.add(SkelligTestStepKeyViewElement(it as NavigatablePsiElement)) }
                }
            return treeElements.toTypedArray()
        }
        return emptyArray()
    }
}