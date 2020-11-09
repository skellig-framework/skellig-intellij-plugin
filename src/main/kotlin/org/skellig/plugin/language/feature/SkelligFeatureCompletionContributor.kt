package org.skellig.plugin.language.feature

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext


class SkelligFeatureCompletionContributor : CompletionContributor() {

    val keywords = setOf("Name", "Test", "Data")

    init {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                object : CompletionProvider<CompletionParameters>() {
                    override fun addCompletions(parameters: CompletionParameters,
                                                context: ProcessingContext,
                                                resultSet: CompletionResultSet) {
                        val suggestions = keywords.filter { item -> item.startsWith(resultSet.prefixMatcher.prefix) }
                        suggestions.forEach { item -> resultSet.addElement(LookupElementBuilder.create(item)) }
                    }
                }
        )
    }
}