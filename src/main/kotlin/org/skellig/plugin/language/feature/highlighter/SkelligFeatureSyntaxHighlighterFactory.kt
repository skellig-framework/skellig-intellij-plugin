package org.skellig.plugin.language.feature.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

class SkelligFeatureSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    @NotNull
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return SkelligFeatureSyntaxHighlighter()
    }
}