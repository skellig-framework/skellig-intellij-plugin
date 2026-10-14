package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.teststep.psi.SkelligTestStepArg
import org.skellig.plugin.language.teststep.psi.SkelligTestStepExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class SkelligTestStepRunTestReference(
    function: SkelligTestStepFunctionExpression,
) : PsiReferenceBase<SkelligTestStepFunctionExpression>(function),
    PsiPolyVariantReference {
    override fun getRangeInElement(): TextRange {
        val expressions = element.argList.first().expressionList
        val quotes = if (expressions.size == 1) 1 else 0
        return TextRange(
            expressions.first().textOffset - element.textOffset + quotes,
            expressions.last().textRange.endOffset - element.textOffset - quotes,
        )
    }

    override fun getCanonicalText(): String = sampleName(element.argList.first()).orEmpty()

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ResolveCache.getInstance(element.project).resolveWithCaching(this, RESOLVER, false, incompleteCode)

    override fun resolve(): PsiElement? = multiResolve(false).singleOrNull()?.element

    override fun isReferenceTo(target: PsiElement): Boolean =
        multiResolve(false).any { element.manager.areElementsEquivalent(it.element, target) }

    private fun findTargets(): Array<ResolveResult> {
        val sample = canonicalText
        val file = element.containingFile
        val results = mutableListOf<ResolveResult>()

        fun collect(children: Array<PsiElement>) {
            for (step in children.filterIsInstance<SkelligTestStepTestStepNameExpression>()) {
                ProgressManager.checkCanceled()
                if (matches(step.testStepName, sample)) results.add(PsiElementResolveResult(step.testStepName))
            }
        }
        collect(file.children)
        if (!DumbService.isDumb(element.project)) {
            val manager = PsiManager.getInstance(element.project)
            FileBasedIndex.getInstance().processValues(
                SkelligTestStepStepIndex.INDEX_ID,
                true,
                null,
                { virtualFile, _ ->
                    ProgressManager.checkCanceled()
                    val psiFile = manager.findFile(virtualFile)
                    if (psiFile != null && psiFile != file) collect(psiFile.children)
                    true
                },
                element.resolveScope,
            )
        }
        return results.toTypedArray()
    }

    companion object {
        private val RESOLVER =
            ResolveCache.PolyVariantResolver<SkelligTestStepRunTestReference> { reference, _ -> reference.findTargets() }

        fun sampleName(argument: SkelligTestStepArg): String? {
            val terms = mutableListOf(mutableListOf<SkelligTestStepExpression>())
            for (expression in argument.expressionList) {
                if (expression.text == "+") {
                    if (terms.last().isEmpty()) return null
                    terms.add(mutableListOf())
                } else {
                    terms.last().add(expression)
                }
            }
            if (terms.last().isEmpty()) return null
            val sample = StringBuilder()
            var hasLiteral = false
            for (term in terms) {
                val literal = term.singleOrNull()?.takeIf { it.firstChild.node.elementType == SkelligTestStepTypes.STRING }
                if (literal != null) {
                    hasLiteral = true
                    sample.append(StringUtil.unescapeStringCharacters(StringUtil.unquoteString(literal.text)))
                } else {
                    // Use a stable alphanumeric sample for each dynamic operand,
                    // including property chains, without evaluating user code.
                    sample.append('a')
                }
            }
            return if (hasLiteral) sample.toString() else null
        }

        fun matches(
            declaration: SkelligTestStepTestStepName,
            sample: String,
        ): Boolean {
            val pattern = StringUtil.unescapeStringCharacters(StringUtil.unquoteString(declaration.text))
            if (pattern == sample) return true
            return try {
                Pattern.compile(pattern).matcher(sample).matches()
            } catch (_: PatternSyntaxException) {
                false
            }
        }
    }
}
