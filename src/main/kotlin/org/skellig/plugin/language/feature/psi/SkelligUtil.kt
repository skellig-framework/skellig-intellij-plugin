package org.skellig.plugin.language.feature.psi

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*
import org.skellig.plugin.language.feature.psi.impl.SkelligFileImpl
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName

object SkelligUtil {
    fun getFeatureLanguage(skelligFile: SkelligFile?): String {
        return skelligFile?.getLocaleLanguage() ?: SkelligFileImpl.defaultLocale
    }

    fun getTestStepName(element: PsiElement): String {
        return when (element) {
            is SkelligTestStepTestStepName -> element.name
            is PsiLiteralExpression -> element.value?.toString() ?: ""
            is KtStringTemplateExpression -> extractTextFromKotlinString(element)
            is PsiAnnotation -> {
                PsiTreeUtil.getChildrenOfType(element, PsiAnnotationParameterList::class.java)?.first()?.let { paramList ->
                    PsiTreeUtil.getChildrenOfType(paramList, PsiNameValuePair::class.java)
                        ?.find { pair -> pair.text.startsWith("name") || pair.children.isNotEmpty() }
                        ?.let {
                            extractTextFromJavaString(PsiTreeUtil.getChildrenOfType(it, PsiLiteralExpression::class.java)?.first())
                        } ?: ""
                } ?: ""
            }

            is KtAnnotationEntry -> {
                PsiTreeUtil.getChildrenOfType(element, KtValueArgumentList::class.java)?.first()?.let { paramList ->
                    PsiTreeUtil.getChildrenOfType(paramList, KtValueArgument::class.java)
                        ?.find { pair -> pair.text.startsWith("name") || pair.children.isNotEmpty() }
                        ?.let {
                            extractTextFromKotlinString(PsiTreeUtil.getChildrenOfType(it, KtStringTemplateExpression::class.java)?.first())
                        } ?: ""
                } ?: ""
            }

            else -> ""
        }
    }

    fun extractTextFromKotlinString(element: KtStringTemplateExpression?): String {
        return element?.entries?.joinToString("") {
            if (it is KtEscapeStringTemplateEntry) it.unescapedValue
            else it.text
        } ?: ""
    }

    fun extractTextFromJavaString(element: PsiLiteralExpression?): String {
        return if (element?.text?.startsWith("\"") == true) element.text.substring(1, element.text.length - 1) else element?.text ?: ""
    }
}