package org.skellig.plugin.language.feature.steps.reference

import com.intellij.lang.LighterAST
import com.intellij.openapi.util.io.DataInputOutputUtilRt
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.PsiDependentFileContent
import com.intellij.util.io.BooleanDataDescriptor
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
import com.intellij.util.text.StringSearcher
import java.io.DataInput
import java.io.DataOutput

abstract class AbstractStepIndex : FileBasedIndexExtension<Boolean, List<Int>>() {

    companion object {

        private val DATA_EXTERNALIZER = object : DataExternalizer<List<Int>> {
            override fun read(input: DataInput): List<Int> {
                return DataInputOutputUtilRt.readSeq(input) { DataInputOutputUtilRt.readINT(input) }
            }

            override fun save(out: DataOutput, value: List<Int>) {
                DataInputOutputUtilRt.writeSeq(out, value) { descriptor: Int -> DataInputOutputUtilRt.writeINT(out, descriptor) }
            }
        }
    }

    override fun getIndexer(): DataIndexer<Boolean, List<Int>, FileContent> {
        return DataIndexer {inputData: FileContent ->
            val text = inputData.contentAsText
            if (!hasCucumberImport(text)) {
                emptyMap<Boolean, List<Int>>()
            }
            val lighterAst = (inputData as PsiDependentFileContent).lighterAST
            val result = getStepDefinitionOffsets(lighterAst, text)
            val resultMap: MutableMap<Boolean, List<Int>> = HashMap()
            resultMap[true] = result
            resultMap
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<Boolean> {
        return BooleanDataDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<List<Int>> {
        return DATA_EXTERNALIZER
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    protected abstract val packagesToScan: Array<String>
    private fun hasCucumberImport(text: CharSequence): Boolean {
        for (pkg in packagesToScan) {
            val searcher = StringSearcher(pkg, true, true)
            if (searcher.scan(text) > 0) {
                return true
            }
        }
        return false
    }

    protected abstract fun getStepDefinitionOffsets(lighterAst: LighterAST, text: CharSequence): List<Int>

}