package org.skellig.plugin.language.testdata

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.testdata.psi.SkelligTestDataFile


class SkelligTestDataStructureViewModel(psiFile: PsiFile) :
        StructureViewModelBase(psiFile, SkelligTestDataStructureViewElement(psiFile)), StructureViewModel.ElementInfoProvider {

    @NotNull
    override fun getSorters(): Array<Sorter> {
        return arrayOf(Sorter.ALPHA_SORTER)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        return true
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        return element.value is SkelligTestDataFile
    }
}