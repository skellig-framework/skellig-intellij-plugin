package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile

class DefaultEnterHandlerDelegate : EnterHandlerDelegate {
    override fun preprocessEnter(p0: PsiFile, p1: Editor, p2: Ref<Int>, p3: Ref<Int>, p4: DataContext, p5: EditorActionHandler?): EnterHandlerDelegate.Result {
        return EnterHandlerDelegate.Result.Default
    }

    override fun postProcessEnter(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result {
        val caretOffset = editor.caretModel.offset
        val lineNumber = editor.document.getLineNumber(caretOffset)

        if (lineNumber > 0) {
            val newLineStartOffset = editor.document.getLineStartOffset(lineNumber)
            val previousLineStartOffset = editor.document.getLineStartOffset(lineNumber - 1)
            val previousLineEndOffset = editor.document.getLineEndOffset(lineNumber - 1)

            // Get the leading whitespaces from the previous line
            val previousLineText = editor.document.charsSequence.subSequence(previousLineStartOffset, previousLineEndOffset)
            var shiftNumber = 0
            if (previousLineText.isNotEmpty() && previousLineText.last() == '{' || previousLineText.last() == '[') {
                val nextLineStartOffset = editor.document.getLineStartOffset(lineNumber + 1)
                val nextLineEndOffset = editor.document.getLineEndOffset(lineNumber + 1)
                val nextLineText = editor.document.charsSequence.subSequence(nextLineStartOffset, nextLineEndOffset)
                shiftNumber = if (nextLineText.last() != '}' || previousLineText.last() != ']') countLeadingWhitespace(nextLineText)
                else countLeadingWhitespace(previousLineText)
            } else {
                shiftNumber = countLeadingWhitespace(previousLineText)
            }

            // Move the caret to align with the previous line's text
            editor.caretModel.moveToOffset(newLineStartOffset + shiftNumber)
        }

        return EnterHandlerDelegate.Result.Default
    }

    private fun countLeadingWhitespace(lineText: CharSequence): Int {
        var count = 0
        while (count < lineText.length && Character.isWhitespace(lineText[count])) {
            count++
        }
        return count
    }
}
