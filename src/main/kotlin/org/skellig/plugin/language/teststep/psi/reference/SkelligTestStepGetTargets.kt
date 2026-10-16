package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.skellig.plugin.language.teststep.SkelligTestStepPsiImplUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepArg
import org.skellig.plugin.language.teststep.psi.SkelligTestStepExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepMap
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepValue

object SkelligTestStepGetTargets {
    private val BARE_KEY = Regex("[a-zA-Z0-9_-]+")

    fun literalName(element: PsiElement): String? {
        val expressions =
            when (element) {
                is SkelligTestStepValue -> element.expressionList
                is SkelligTestStepArg -> element.expressionList
                else -> return null
            }
        val expression = expressions.singleOrNull() ?: return null
        return unquote(expression.text)
    }

    private fun unquote(text: String): String? =
        when {
            BARE_KEY.matches(text) -> text
            text.startsWith('"') && text.endsWith('"') -> StringUtil.unescapeStringCharacters(text.substring(1, text.length - 1))
            else -> null
        }

    fun idName(element: SkelligTestStepValue): String? {
        val pair = element.parent as? SkelligTestStepPair ?: return null
        if (pair.parent !is SkelligTestStepTestStepNameExpression || pair.key.text != "id") return null
        return literalName(element)?.takeIf { it.isNotEmpty() }
    }

    fun stateName(element: SkelligTestStepKey): String? {
        val pair = element.parent as? SkelligTestStepPair ?: return null
        val map = pair.parent as? SkelligTestStepMap ?: return null
        val state = map.parent as? SkelligTestStepPair ?: return null
        if (state.key.text != "state" || state.parent !is SkelligTestStepTestStepNameExpression) return null
        return unquote(element.text)?.takeIf { it.isNotEmpty() }
    }

    fun targets(
        file: PsiFile,
        ids: Boolean,
    ): List<PsiElement> =
        file.children.filterIsInstance<SkelligTestStepTestStepNameExpression>().flatMap { step ->
            step.pairList.flatMap { pair ->
                if (ids) {
                    listOfNotNull(pair.value?.takeIf { idName(it) != null })
                } else if (pair.key.text == "state") {
                    pair.map?.pairList?.map { it.key } ?: emptyList()
                } else {
                    emptyList()
                }
            }
        }

    fun targetName(element: PsiElement): String? =
        when (element) {
            is SkelligTestStepValue -> idName(element)
            is SkelligTestStepKey -> stateName(element)
            else -> null
        }

    fun renameLiteral(
        expression: SkelligTestStepExpression,
        newName: String,
    ): PsiElement {
        val literal =
            if (expression.text.startsWith('"') || !BARE_KEY.matches(newName)) {
                "\"${StringUtil.escapeStringCharacters(newName)}\""
            } else {
                newName
            }
        val file = SkelligTestStepPsiImplUtil.createFile(expression.project, "name(\"rename\") { id = $literal }")
        val replacement =
            (file.firstChild as SkelligTestStepTestStepNameExpression)
                .pairList
                .single()
                .value!!
                .expressionList
                .single()
        return expression.replace(replacement)
    }
}
