package org.skellig.plugin.language.feature.psi.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.skellig.plugin.language.feature.psi.PlainSkelligKeywordProvider

class SkelligSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(
        project: Project?,
        virtualFile: VirtualFile?,
    ): SyntaxHighlighter = SkelligSyntaxHighlighter(PlainSkelligKeywordProvider())
}
