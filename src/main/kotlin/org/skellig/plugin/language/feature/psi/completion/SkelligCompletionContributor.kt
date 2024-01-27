package org.skellig.plugin.language.feature.psi.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class SkelligCompletionContributor : CompletionContributor() {

    private val keywords = listOf(
        "Feature", "Scenario", "Examples"
    )

    init {
        extend(
            CompletionType.BASIC, PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    parameters.originalPosition?.text?.let { currentText ->
                        keywords.filter { it.startsWith(currentText) }
                            .forEach {
                                result.addElement(LookupElementBuilder.create(it).withInsertHandler { context, _ ->
                                    context.document.insertString(context.selectionEndOffset, ":")
                                })
                            }
                    }
                }
            }
        )
    }
}