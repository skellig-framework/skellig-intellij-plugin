package org.jetbrains.plugins.skellig.teststep.psi.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.cucumber.psi.PlainGherkinKeywordProvider
import org.jetbrains.plugins.skellig.teststep.psi.PlainSkelligTestStepKeywordProvider

class SkelligTestStepSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return SkelligTestStepSyntaxHighlighter(PlainSkelligTestStepKeywordProvider())
    }
}