package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import org.skellig.plugin.language.feature.psi.SkelligElementTypes
import org.skellig.plugin.language.teststep.psi.*


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

            override fun visitTestStepPropertyKeywords(o: SkelligTestStepTestStepPropertyKeywords) {
                super.visitTestStepPropertyKeywords(o)
                if (o.parentOfType<SkelligTestStepPair>()?.parent?.elementType != SkelligTestStepTypes.TEST_STEP_NAME_EXPRESSION) {
                    setHighlighting(o, holder, SkelligTestStepHighlighter.TEXT)
                }
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