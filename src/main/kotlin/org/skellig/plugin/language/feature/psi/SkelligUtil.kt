package org.skellig.plugin.language.feature.psi

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.skellig.plugin.language.feature.psi.impl.SkelligFileImpl
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName

object SkelligUtil {
    fun getFeatureLanguage(skelligFile: SkelligFile?): String {
        return skelligFile?.getLocaleLanguage() ?: SkelligFileImpl.defaultLocale
    }

    fun getTestStepName(element: PsiElement): String {
        return when (element) {
            is SkelligTestStepTestStepName -> element.name
            is PsiAnnotation -> {
                PsiTreeUtil.getChildrenOfType(element, PsiAnnotationParameterList::class.java)?.first()?.let { paramList ->
                    PsiTreeUtil.getChildrenOfType(paramList, PsiNameValuePair::class.java)
                        ?.find { pair -> pair.text.startsWith("name") || pair.children.isNotEmpty() }
                        ?.let { name ->
                            val text = PsiTreeUtil.getChildrenOfType(name, PsiLiteralExpression::class.java)?.first()?.text
                            if (text?.startsWith("\"") == true) text.substring(1, text.length - 1) else text
                        } ?: ""
                } ?: ""
            }

            else -> ""
        }
    }
}