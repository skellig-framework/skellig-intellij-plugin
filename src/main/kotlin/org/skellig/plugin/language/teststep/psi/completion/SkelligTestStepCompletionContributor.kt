package org.skellig.plugin.language.teststep.psi.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

private const val NAME = "name"

class SkelligTestStepCompletionContributor : CompletionContributor() {
    private val keywordsWithComplexValue = setOf("values", "validate", "body", "request", "message", "payload", "state")
    private val keywords =
        keywordsWithComplexValue.union(
            listOf(
                NAME,
                "method",
                "headers",
                "url",
                "services",
                "query",
                "form",
                "username",
                "password",
                "parent",
                "execution",
                "timeout",
                "delay",
                "attempts",
                "id",
                "servers",
                "table",
                "command",
                "where",
                "data",
                "provider",
                "test",
                "passed",
                "failed",
                "protocol",
                "respondTo",
                "consumeFrom",
                "properties",
                "sendTo",
                "readFrom",
                "bufferSize",
                "hosts",
                "args",
                "rps",
                "timeToRun",
                "before",
                "after",
                "run",
            ),
        )

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet,
                ) {
                    parameters.originalPosition?.text?.let { currentText ->
                        keywords
                            .filter { it.startsWith(currentText) }
                            .forEach {
                                var element = LookupElementBuilder.create(it)
                                if (keywordsWithComplexValue.contains(it)) {
                                    element =
                                        element.withInsertHandler { context, _ ->
                                            context.document.insertString(context.selectionEndOffset, " { }")
                                            context.editor.caretModel.moveToOffset(context.selectionEndOffset - 1)
                                        }
                                } else if (NAME == it && isTopLevel(parameters)) {
                                    element =
                                        element.withInsertHandler { context, _ ->
                                            val insertionOffset = context.selectionEndOffset
                                            val testName = "Test name"
                                            context.document.insertString(insertionOffset, " (\"$testName\") {\n}")
                                            val nameStart = insertionOffset + " (\"".length
                                            context.editor.caretModel.moveToOffset(nameStart)
                                            context.editor.selectionModel.setSelection(nameStart, nameStart + testName.length)
                                            context.setAddCompletionChar(false)
                                        }
                                }
                                result.addElement(element)
                            }
                    }
                }
            },
        )
    }

    private fun isTopLevel(parameters: CompletionParameters): Boolean {
        // Incomplete declarations may not have object PSI yet, so inspect delimiters before the caret.
        val lexer = SkelligTestStepLexerAdapter()
        lexer.start(parameters.originalFile.text, 0, parameters.offset)
        var depth = 0
        while (lexer.tokenType != null) {
            when (lexer.tokenType) {
                SkelligTestStepTypes.OBJECT_L_BRACKET,
                SkelligTestStepTypes.REFERENCE_BRACKET,
                SkelligTestStepTypes.FUNCTION_L_BRACKET,
                SkelligTestStepTypes.ARRAY_L_BRACKET,
                -> depth++
                SkelligTestStepTypes.OBJECT_R_BRACKET,
                SkelligTestStepTypes.FUNCTION_R_BRACKET,
                SkelligTestStepTypes.ARRAY_R_BRACKET,
                -> depth = (depth - 1).coerceAtLeast(0)
            }
            lexer.advance()
        }
        return depth == 0
    }
}
