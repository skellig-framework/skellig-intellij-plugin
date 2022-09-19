package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.project.Project
import org.skellig.plugin.language.teststep.psi.PlainSkelligTestStepKeywordProvider

class SkelligTestStepSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return SkelligTestStepSyntaxHighlighter(PlainSkelligTestStepKeywordProvider())
    }
}