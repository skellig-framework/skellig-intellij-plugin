package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.util.io.DataInputOutputUtilRt
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFileType
import java.io.DataInput
import java.io.DataOutput

class SkelligTestStepGetTargetIndex : FileBasedIndexExtension<String, List<Int>>() {
    override fun getName(): ID<String, List<Int>> = INDEX_ID

    override fun getVersion(): Int = 1

    override fun dependsOnFileContent(): Boolean = true

    override fun getInputFilter(): FileBasedIndex.InputFilter = DefaultFileTypeSpecificInputFilter(SkelligTestStepFileType.INSTANCE)

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<List<Int>> =
        object : DataExternalizer<List<Int>> {
            override fun read(input: DataInput): List<Int> = DataInputOutputUtilRt.readSeq(input) { DataInputOutputUtilRt.readINT(input) }

            override fun save(
                out: DataOutput,
                value: List<Int>,
            ) {
                DataInputOutputUtilRt.writeSeq(out, value) { offset -> DataInputOutputUtilRt.writeINT(out, offset) }
            }
        }

    override fun getIndexer(): DataIndexer<String, List<Int>, FileContent> =
        DataIndexer { content ->
            val result = mutableMapOf<String, MutableList<Int>>()
            for (ids in listOf(true, false)) {
                for (target in SkelligTestStepGetTargets.targets(content.psiFile, ids)) {
                    val name = SkelligTestStepGetTargets.targetName(target) ?: continue
                    result.getOrPut(key(name, ids)) { mutableListOf() }.add(target.textOffset)
                }
            }
            result
        }

    companion object {
        val INDEX_ID = ID.create<String, List<Int>>("org.skellig.getTargets")

        fun key(
            name: String,
            ids: Boolean,
        ): String = if (ids) "id:$name" else "state:$name"
    }
}
