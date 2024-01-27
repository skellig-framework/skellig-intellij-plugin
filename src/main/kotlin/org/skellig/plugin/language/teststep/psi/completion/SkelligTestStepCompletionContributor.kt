package org.skellig.plugin.language.teststep.psi.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext


private const val NAME = "name"

class SkelligTestStepCompletionContributor : CompletionContributor() {

    private val keywordsWithComplexValue = setOf("values", "validate", "body", "request", "message", "payload")
    private val keywords = keywordsWithComplexValue.union(listOf(
        NAME,
        "method", "headers", "url", "services", "query", "form", "username", "password",
        "parent", "execution", "timeout", "delay", "attempts", "id",
        "servers", "table", "command", "where", "data", "provider",
        "test", "passed", "failed",
        "protocol", "respondTo", "consumeFrom", "properties", "sendTo", "readFrom", "bufferSize",
        "hosts", "args",
        "rps", "timeToRun", "before", "after", "run"
    ))

    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    parameters.originalPosition?.text?.let { currentText ->
                        keywords.filter { it.startsWith(currentText) }
                            .forEach {
                                var element = LookupElementBuilder.create(it)
                                if (keywordsWithComplexValue.contains(it)) {
                                    element = element.withInsertHandler() { context, _ ->
                                        context.document.insertString(context.selectionEndOffset, " { }");
                                        context.editor.caretModel.moveToOffset(context.selectionEndOffset - 1);
                                    }
                                } else if (NAME == it) {
                                    element = element.withInsertHandler() { context, _ ->
                                        context.document.insertString(context.selectionEndOffset, " (\"\") { }");
                                        context.editor.caretModel.moveToOffset(context.selectionEndOffset - 1);
                                    }
                                }
                                result.addElement(element)
                            }
                    }
                }
            }
        )
    }
}