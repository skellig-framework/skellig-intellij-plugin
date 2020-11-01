package org.skellig.plugin.language.testdata

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import org.jetbrains.annotations.NotNull


class SkelligTestDataFileTypeFactory : FileTypeFactory() {

    override fun createFileTypes(@NotNull fileTypeConsumer: FileTypeConsumer) {
        fileTypeConsumer.consume(SkelligTestDataFileType.INSTANCE)
    }
}