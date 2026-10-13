package org.skellig.plugin.language.feature.psi

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiAnnotationParameterList
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.skellig.plugin.language.feature.psi.impl.SkelligFileImpl
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName

object SkelligUtil {
    fun getFeatureLanguage(skelligFile: SkelligFile?): String = skelligFile?.getLocaleLanguage() ?: SkelligFileImpl.defaultLocale

    fun getTestStepName(element: PsiElement): String =
        when (element) {
            is SkelligTestStepTestStepName -> element.name ?: ""
            is PsiLiteralExpression -> element.value?.toString() ?: ""
            is KtStringTemplateExpression -> extractTextFromKotlinString(element)
            is PsiAnnotation -> {
                PsiTreeUtil.getChildrenOfType(element, PsiAnnotationParameterList::class.java)?.first()?.let { paramList ->
                    PsiTreeUtil
                        .getChildrenOfType(paramList, PsiNameValuePair::class.java)
                        ?.find { pair -> pair.text.startsWith("name") || pair.children.isNotEmpty() }
                        ?.let {
                            extractTextFromJavaString(PsiTreeUtil.getChildrenOfType(it, PsiLiteralExpression::class.java)?.first())
                        } ?: ""
                } ?: ""
            }

            is KtAnnotationEntry -> {
                PsiTreeUtil.getChildrenOfType(element, KtValueArgumentList::class.java)?.first()?.let { paramList ->
                    PsiTreeUtil
                        .getChildrenOfType(paramList, KtValueArgument::class.java)
                        ?.find { pair -> pair.text.startsWith("name") || pair.children.isNotEmpty() }
                        ?.let {
                            extractTextFromKotlinString(PsiTreeUtil.getChildrenOfType(it, KtStringTemplateExpression::class.java)?.first())
                        } ?: ""
                } ?: ""
            }

            else -> ""
        }

    fun extractTextFromKotlinString(element: KtStringTemplateExpression?): String =
        element?.entries?.joinToString("") {
            if (it is KtEscapeStringTemplateEntry) {
                it.unescapedValue
            } else {
                it.text
            }
        } ?: ""

    fun extractTextFromJavaString(element: PsiLiteralExpression?): String =
        if (element?.text?.startsWith("\"") == true) element.text.substring(1, element.text.length - 1) else element?.text ?: ""
}
