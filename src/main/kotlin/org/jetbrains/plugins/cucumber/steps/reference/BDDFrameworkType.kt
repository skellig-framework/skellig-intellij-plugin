package org.jetbrains.plugins.cucumber.steps.reference

import com.intellij.openapi.fileTypes.FileType

class BDDFrameworkType
/**
 * @param fileType file type to be used as step definitions for this framework
 */ @JvmOverloads constructor(
    /**
     * @return file type to be used as step definitions for this framework
     */
    val fileType: FileType,
    /**
     * @return additional information about this framework to be displayed to user (when filetype is not enough)
     */
    val additionalInfo: String? = null
) {
    override fun toString(): String {
        return "BDDFrameworkType{" +
                "myFileType=" + fileType +
                ", myAdditionalInfo='" + additionalInfo + '\'' +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is BDDFrameworkType) return false
        val type = o
        if (if (additionalInfo != null) additionalInfo != type.additionalInfo else type.additionalInfo != null) return false
        return if (fileType != type.fileType) false else true
    }

    override fun hashCode(): Int {
        var result = fileType.hashCode()
        result = 31 * result + if (additionalInfo != null) additionalInfo.hashCode() else 0
        return result
    }
}