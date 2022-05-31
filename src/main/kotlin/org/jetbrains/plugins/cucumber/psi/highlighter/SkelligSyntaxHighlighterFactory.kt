package org.jetbrains.plugins.cucumber.psi.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.cucumber.psi.PlainGherkinKeywordProvider
import org.jetbrains.plugins.cucumber.psi.highlighter.SkelligSyntaxHighlighter

class SkelligSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return SkelligSyntaxHighlighter(PlainGherkinKeywordProvider())
    }
}