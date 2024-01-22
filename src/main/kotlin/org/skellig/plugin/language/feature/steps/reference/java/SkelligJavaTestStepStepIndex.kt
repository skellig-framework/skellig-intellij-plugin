package org.skellig.plugin.language.feature.steps.reference.java

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.lang.LighterAST
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID
import org.skellig.plugin.language.feature.steps.reference.AbstractStepIndex

class SkelligJavaTestStepStepIndex : AbstractStepIndex() {

    companion object {
        val INDEX_ID = ID.create<Boolean, List<Int>>("org.skellig.teststep.java")
    }

    override val packagesToScan: Array<String> = emptyArray()

    override fun getStepDefinitionOffsets(lighterAst: LighterAST, text: CharSequence): List<Int> {
        return emptyList()
    }

    override fun getName(): ID<Boolean, List<Int>> = INDEX_ID

    override fun getVersion(): Int = 1

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(JavaFileType.INSTANCE) {
        }
    }
}