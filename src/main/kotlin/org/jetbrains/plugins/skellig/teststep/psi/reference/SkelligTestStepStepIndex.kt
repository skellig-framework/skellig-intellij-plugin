package org.jetbrains.plugins.skellig.teststep.psi.reference

import com.intellij.lang.LighterAST
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID
import org.jetbrains.plugins.cucumber.steps.reference.AbstractStepIndex
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepFileType

class SkelligTestStepStepIndex : AbstractStepIndex() {

    companion object {
        val INDEX_ID = ID.create<Boolean, List<Int>>("org.skellig.teststep")
    }

    override val packagesToScan: Array<String> = emptyArray()

    override fun getStepDefinitionOffsets(lighterAst: LighterAST, text: CharSequence): List<Int> {
        return emptyList()
    }

    override fun getName(): ID<Boolean, List<Int>> = INDEX_ID

    override fun getVersion(): Int = 1

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(SkelligTestStepFileType.INSTANCE) {
        }
    }
}