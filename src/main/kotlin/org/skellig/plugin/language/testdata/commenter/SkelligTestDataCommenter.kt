package org.skellig.plugin.language.testdata.commenter

import com.intellij.lang.Commenter
import org.jetbrains.annotations.Nullable

class SkelligTestDataCommenter : Commenter {

    @Nullable
    override fun getLineCommentPrefix(): String? {
        return "//"
    }

    @Nullable
    override fun getBlockCommentPrefix(): String? {
        return "/*"
    }

    @Nullable
    override fun getBlockCommentSuffix(): String? {
        return "*/"
    }

    @Nullable
    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    @Nullable
    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }
}