package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepReferenceKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepVisitor


class SkelligTestStepFunctionAnnotator() : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(object : SkelligTestStepVisitor() {
            override fun visitFunctionName(o: SkelligTestStepFunctionName) {
                super.visitFunctionName(o)
                setHighlighting(o, holder, SkelligTestStepHighlighter.FUNCTION)
            }

            override fun visitReferenceKey(o: SkelligTestStepReferenceKey) {
                super.visitReferenceKey(o)
                setHighlighting(o, holder, SkelligTestStepHighlighter.REFERENCE)
            }
        })
    }

    private fun setHighlighting(
        element: PsiElement, holder: AnnotationHolder,
        key: TextAttributesKey
    ) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(key)
            .create()
    }

}