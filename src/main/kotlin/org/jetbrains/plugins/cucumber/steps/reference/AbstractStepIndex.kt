package org.jetbrains.plugins.cucumber.steps.reference

import com.intellij.lang.LighterAST
import com.intellij.openapi.util.io.DataInputOutputUtilRt
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.BooleanDataDescriptor
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
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
        return DataIndexer {
            emptyMap<Boolean, List<Int>>()
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

    protected abstract fun getStepDefinitionOffsets(lighterAst: LighterAST, text: CharSequence): List<Int>

}