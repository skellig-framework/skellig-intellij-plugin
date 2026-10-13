package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
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

    private fun setHighlighting(
        element: PsiElement,
        holder: AnnotationHolder,
        key: TextAttributesKey,
    ) {
        holder
            .newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(key)
            .create()
    }
}
