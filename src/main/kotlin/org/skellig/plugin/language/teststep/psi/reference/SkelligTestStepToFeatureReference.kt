package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.psi.SkelligFile
import org.skellig.plugin.language.feature.psi.SkelligFileType
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

open class SkelligTestStepToFeatureReference(
    myElement: PsiElement,
    private val range: TextRange,
) : SkelligTestStepSimpleReference(myElement),
    PsiPolyVariantReference {
    companion object {
        private val RESOLVER = MyResolver()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ResolveCache
            .getInstance(
                element.project,
            ).resolveWithCaching<SkelligTestStepToFeatureReference>(this, RESOLVER, false, incompleteCode)

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun isSoft(): Boolean = false

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolvedResults = multiResolve(false)
        for (rr in resolvedResults) {
            if (element.manager.areElementsEquivalent(rr.element, element)) {
                return true
            }
        }
        return false
    }

    override fun getCanonicalText(): String = element.text

    override fun handleElementRename(newElementName: String): PsiElement = element

    override fun bindToElement(element: PsiElement): PsiElement = element

    override fun getRangeInElement(): TextRange = range

    fun multiResolveInner(): Array<ResolveResult> {
        val usages =
            CachedValuesManager.getCachedValue(element) {
                val results = mutableSetOf<PsiElement>()
                val project = element.project
                val module = ModuleUtilCore.findModuleForPsiElement(element)
                val featureScope =
                    module?.getModuleWithDependenciesAndLibrariesScope(true)?.uniteWith(ProjectScope.getLibrariesScope(project))
                        ?: element.resolveScope
                val name = element as? SkelligTestStepTestStepName
                val pattern =
                    try {
                        Pattern.compile(SkelligUtil.getTestStepName(element))
                    } catch (_: PatternSyntaxException) {
                        null
                    }

                fun matches(text: String): Boolean =
                    if (name != null) SkelligTestStepRunTestReference.matches(name, text) else pattern?.matcher(text)?.matches() == true

                if (!DumbService.isDumb(project)) {
                    val manager = PsiManager.getInstance(project)
                    FileTypeIndex.processFiles(
                        SkelligFileType.INSTANCE,
                        { file ->
                            ProgressManager.checkCanceled()
                            val featureFile = manager.findFile(file) as? SkelligFile
                            if (featureFile != null) {
                                for (step in PsiTreeUtil.findChildrenOfType(featureFile, SkelligFeatureStep::class.java)) {
                                    if (matches(step.substitutedName.orEmpty())) results.add(step)
                                }
                            }
                            true
                        },
                        featureScope,
                    )
                    if (name != null) {
                        val usageScope = GlobalSearchScope.projectScope(project)
                        collectRunTestUsages(element.containingFile, name, results)
                        FileBasedIndex.getInstance().processValues(
                            SkelligTestStepStepIndex.INDEX_ID,
                            true,
                            null,
                            { file, _ ->
                                ProgressManager.checkCanceled()
                                val psiFile = manager.findFile(file)
                                if (psiFile != null && psiFile != element.containingFile) collectRunTestUsages(psiFile, name, results)
                                true
                            },
                            usageScope,
                        )
                        collectLanguageUsages(usageScope, ::matches, results)
                    }
                } else if (name != null) {
                    collectRunTestUsages(element.containingFile, name, results)
                }
                CachedValueProvider.Result.create(
                    results.map { SkelligFeatureStepDefinition(it) },
                    PsiModificationTracker.MODIFICATION_COUNT,
                    DumbService.getInstance(project).modificationTracker,
                )
            }
        return usages.mapNotNull { it.getElement() }.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    private fun collectLanguageUsages(
        scope: GlobalSearchScope,
        matches: (String) -> Boolean,
        results: MutableSet<PsiElement>,
    ) {
        val languages = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, JavaFileType.INSTANCE, KotlinFileType.INSTANCE)
        val files = mutableSetOf<PsiFile>()
        // Keep the indexed candidate search behind the public PSI search API.
        PsiSearchHelper.getInstance(element.project).processAllFilesWithWord(
            "run",
            languages,
            { file ->
                files.add(file)
                true
            },
            true,
        )
        for (psiFile in files) {
            ProgressManager.checkCanceled()
            if (psiFile.fileType == JavaFileType.INSTANCE) {
                for (call in PsiTreeUtil.findChildrenOfType(psiFile, PsiMethodCallExpression::class.java)) {
                    val argument = call.argumentList.expressions.firstOrNull() as? PsiLiteralExpression
                    val text = argument?.value as? String
                    if (call.methodExpression.referenceName == "run" && text != null && matches(text)) results.add(argument)
                }
            } else {
                for (call in PsiTreeUtil.findChildrenOfType(psiFile, KtCallExpression::class.java)) {
                    val argument = call.valueArguments.firstOrNull()?.getArgumentExpression() as? KtStringTemplateExpression
                    if (call.calleeExpression?.text == "run" &&
                        argument != null &&
                        argument.entries.all { it is KtLiteralStringTemplateEntry || it is KtEscapeStringTemplateEntry } &&
                        matches(SkelligUtil.extractTextFromKotlinString(argument))
                    ) {
                        results.add(argument)
                    }
                }
            }
        }
    }

    private fun collectRunTestUsages(
        file: PsiFile,
        name: SkelligTestStepTestStepName,
        results: MutableSet<PsiElement>,
    ) {
        for (call in PsiTreeUtil.findChildrenOfType(file, SkelligTestStepFunctionExpression::class.java)) {
            ProgressManager.checkCanceled()
            if (call.functionName.text != "runTest") continue
            val sample = call.argList.firstOrNull()?.let { SkelligTestStepRunTestReference.sampleName(it) } ?: continue
            if (SkelligTestStepRunTestReference.matches(name, sample)) results.add(call)
        }
    }

    override fun equals(other: Any?): Boolean =
        if (other is SkelligTestStepToFeatureReference) {
            element == other.element
        } else {
            false
        }

    override fun hashCode(): Int = element.hashCode()

    private class MyResolver : ResolveCache.PolyVariantResolver<SkelligTestStepToFeatureReference?> {
        override fun resolve(
            ref: SkelligTestStepToFeatureReference,
            incompleteCode: Boolean,
        ): Array<ResolveResult> = ref.multiResolveInner()
    }

    class SkelligFeatureStepDefinition(
        element: PsiElement,
    ) {
        private val myElementPointer: SmartPsiElementPointer<PsiElement> =
            SmartPointerManager
                .getInstance(
                    element.project,
                ).createSmartPsiElementPointer(element)

        fun getElement(): PsiElement? = myElementPointer.element

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as SkelligFeatureStepDefinition
            return myElementPointer == that.myElementPointer
        }

        override fun hashCode(): Int = myElementPointer.hashCode()
    }
}
