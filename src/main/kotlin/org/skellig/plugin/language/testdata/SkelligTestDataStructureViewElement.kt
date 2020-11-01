package org.skellig.plugin.language.testdata

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.testdata.psi.SkelligTestDataFile
import java.util.*

class SkelligTestDataStructureViewElement(element: NavigatablePsiElement) : StructureViewTreeElement, SortableTreeElement {

    private var myElement: NavigatablePsiElement = element

    override fun getValue(): Any? {
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

    @NotNull
    override fun getAlphaSortKey(): String {
        val name = myElement.name
        return name ?: ""
    }

    @NotNull
    override fun getPresentation(): ItemPresentation {
        val presentation = myElement.presentation
        return presentation ?: PresentationData()
    }

    override fun getChildren(): Array<TreeElement> {
        /*if (myElement is SkelligTestDataFile) {
            val properties = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SimpleProperty::class.java)
            val treeElements: MutableList<TreeElement> = ArrayList<TreeElement>(properties.size)
            for (property in properties) {
                treeElements.add(SkelligTestDataStructureViewElement((property as SimplePropertyImpl)))
            }
            return treeElements.toTypedArray()
        }*/
        return arrayOf()
    }
}