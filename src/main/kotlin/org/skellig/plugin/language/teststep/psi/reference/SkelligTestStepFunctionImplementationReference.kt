package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReturnStatement
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.org.objectweb.asm.ClassReader
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.ClassNode
import org.jetbrains.org.objectweb.asm.tree.LdcInsnNode
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionName
import java.io.IOException

class SkelligTestStepFunctionImplementationReference(
    functionName: SkelligTestStepFunctionName,
) : PsiReferenceBase<SkelligTestStepFunctionName>(functionName, TextRange(0, functionName.textLength), true),
    PsiPolyVariantReference {
    override fun getCanonicalText(): String = element.text

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        if (DumbService.isDumb(element.project)) return emptyArray()
        val implementations =
            CachedValuesManager.getCachedValue(element.containingFile) {
                CachedValueProvider.Result.create(
                    findImplementations(),
                    PsiModificationTracker.MODIFICATION_COUNT,
                    ProjectRootManager.getInstance(element.project),
                    DumbService.getInstance(element.project).modificationTracker,
                )
            }
        return implementations[canonicalText].orEmpty().map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun resolve(): PsiElement? = multiResolve(false).singleOrNull()?.element

    override fun isReferenceTo(target: PsiElement): Boolean =
        multiResolve(false).any { element.manager.areElementsEquivalent(it.element, target) }

    override fun getVariants(): Array<Any> = emptyArray()

    private fun findImplementations(): Map<String, List<PsiElement>> {
        val scope = element.resolveScope
        val base = JavaPsiFacade.getInstance(element.project).findClass(EXECUTOR_INTERFACE, scope) ?: return emptyMap()
        val result = mutableMapOf<String, MutableList<PsiElement>>()
        ClassInheritorsSearch.search(base, scope, true).forEach { implementation ->
            ProgressManager.checkCanceled()
            if (!implementation.isInterface && !implementation.hasModifierProperty(PsiModifier.ABSTRACT)) {
                val getter = implementation.findMethodsByName("getFunctionName", true).firstOrNull { it.parameterList.parametersCount == 0 }
                val name = getter?.let { functionName(it) }
                if (!name.isNullOrBlank()) result.getOrPut(name) { mutableListOf() }.add(implementation.navigationElement)
            }
        }
        return result
    }

    private fun functionName(method: PsiMethod): String? {
        val navigation = method.navigationElement
        if (navigation is KtNamedFunction) {
            val body = navigation.bodyExpression
            val expression =
                if (body is KtBlockExpression) {
                    (body.statements.singleOrNull() as? KtReturnExpression)?.returnedExpression
                } else {
                    body
                }
            val string = expression as? KtStringTemplateExpression ?: return null
            if (string.entries.any { it !is KtLiteralStringTemplateEntry && it !is KtEscapeStringTemplateEntry }) return null
            return string.entries.joinToString("") { if (it is KtEscapeStringTemplateEntry) it.unescapedValue else it.text }
        }
        val statement = method.body?.statements?.singleOrNull() as? PsiReturnStatement
        if (statement != null) {
            return JavaPsiFacade
                .getInstance(
                    element.project,
                ).constantEvaluationHelper
                .computeConstantExpression(statement.returnValue) as? String
        }
        val owner = method.containingClass ?: return null
        return compiledFunctionName(owner)
    }

    private fun compiledFunctionName(owner: PsiClass): String? {
        val file = owner.containingFile?.virtualFile ?: return null
        if (file.extension != "class") return null
        val node = ClassNode()
        try {
            ClassReader(file.contentsToByteArray()).accept(node, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        } catch (_: IOException) {
            return null
        } catch (_: IllegalArgumentException) {
            return null
        }
        val getter = node.methods.firstOrNull { it.name == "getFunctionName" && it.desc == "()Ljava/lang/String;" } ?: return null
        val instructions = getter.instructions.toArray().filter { it.opcode >= 0 }
        // A constant getter is LDC <name>; ARETURN. Do not guess names from dynamic code.
        if (instructions.size != 2 || instructions.last().opcode != Opcodes.ARETURN) return null
        return (instructions.first() as? LdcInsnNode)?.cst as? String
    }

    companion object {
        private const val EXECUTOR_INTERFACE = "org.skellig.teststep.processing.value.function.FunctionValueExecutor"
    }
}
