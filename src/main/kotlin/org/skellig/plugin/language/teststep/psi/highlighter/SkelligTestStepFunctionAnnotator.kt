package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepMap
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepReferenceKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestNameKeyword
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepVisitor

class SkelligTestStepFunctionAnnotator : Annotator {
    private val propertyKeywords =
        setOf("name", "values", "state", "validate", "where", "payload", "body", "message", "messages", "request", "test")

    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        highlightValidationResults(element, holder)
        element.accept(
            object : SkelligTestStepVisitor() {
                override fun visitFunctionName(o: SkelligTestStepFunctionName) {
                    super.visitFunctionName(o)
                    setHighlighting(o, holder, SkelligTestStepHighlighter.FUNCTION)
                }

                override fun visitReferenceKey(o: SkelligTestStepReferenceKey) {
                    super.visitReferenceKey(o)
                    setHighlighting(o, holder, SkelligTestStepHighlighter.REFERENCE)
                }

                override fun visitTestNameKeyword(o: SkelligTestStepTestNameKeyword) {
                    super.visitTestNameKeyword(o)
                    setHighlighting(o, holder, SkelligTestStepHighlighter.KEYWORD)
                }

                override fun visitKey(o: SkelligTestStepKey) {
                    super.visitKey(o)
                    val pair = o.parent as? SkelligTestStepPair
                    if (pair?.parent is SkelligTestStepTestStepNameExpression && o.text in propertyKeywords) {
                        setHighlighting(o, holder, SkelligTestStepHighlighter.KEYWORD)
                    }
                }
            },
        )
    }

    private fun highlightValidationResults(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        val dollars = element.children.filterIsInstance<SkelligTestStepExpression>().filter { it.text == "$" }
        if (dollars.isEmpty() || !isInsideValidationBlock(element)) return
        dollars.forEach { dollar ->
            // The lexer splits $result into adjacent "$" and "result" expressions.
            val next = dollar.nextSibling as? SkelligTestStepExpression
            val adjacent =
                next?.takeIf {
                    it.textRange.startOffset == dollar.textRange.endOffset && it.text.firstOrNull()?.isLetterOrDigit() == true
                }
            if (adjacent != null && adjacent.text != "result") return@forEach
            val range = TextRange(dollar.textRange.startOffset, adjacent?.textRange?.endOffset ?: dollar.textRange.endOffset)
            setHighlighting(element, holder, SkelligTestStepHighlighter.KEYWORD, range)
        }
    }

    private fun isInsideValidationBlock(element: PsiElement): Boolean =
        generateSequence(element.parent) { it.parent }
            .filterIsInstance<SkelligTestStepMap>()
            .any { map ->
                val pair = map.parent as? SkelligTestStepPair
                pair?.parent is SkelligTestStepTestStepNameExpression && pair.key.text in setOf("validate", "assert")
            }

    private fun setHighlighting(
        element: PsiElement,
        holder: AnnotationHolder,
        key: TextAttributesKey,
        range: TextRange = element.textRange,
    ) {
        holder
            .newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range)
            .textAttributes(key)
            .create()
    }
}
