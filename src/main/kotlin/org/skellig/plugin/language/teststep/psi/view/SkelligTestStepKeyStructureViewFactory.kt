package org.skellig.plugin.language.teststep.psi.view

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class SkelligTestStepKeyStructureViewFactory : PsiStructureViewFactory {

    override fun getStructureViewBuilder(file: PsiFile): StructureViewBuilder {
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return SkelligTestStepKeyStructureViewModel(editor, file)
            }
        }
    }


}