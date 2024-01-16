package org.skellig.plugin.language.teststep.psi.view

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey


class SimpleStructureViewModel(editor: Editor?, psiFile: PsiFile) :
    StructureViewModelBase(psiFile, editor, SkelligTestStepKeyViewElement(psiFile)), StructureViewModel.ElementInfoProvider {

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        return element.value is SkelligTestStepKey
    }

    override fun getSuitableClasses(): Array<Class<SkelligTestStepKey>> {
       return arrayOf(SkelligTestStepKey::class.java)
    }
}