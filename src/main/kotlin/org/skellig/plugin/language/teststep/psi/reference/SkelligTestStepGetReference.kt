package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepValue

class SkelligTestStepGetReference(
    function: SkelligTestStepFunctionExpression,
) : PsiReferenceBase<SkelligTestStepFunctionExpression>(function),
    PsiPolyVariantReference {
    override fun getRangeInElement(): TextRange {
        val expression =
            element.argList
                .first()
                .expressionList
                .single()
        val offset = expression.textOffset - element.textOffset
        val quotes = if (expression.text.startsWith('"')) 1 else 0
        return TextRange(offset + quotes, offset + expression.textLength - quotes)
    }

    override fun getCanonicalText(): String = SkelligTestStepGetTargets.literalName(element.argList.first()).orEmpty()

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ResolveCache.getInstance(element.project).resolveWithCaching(this, RESOLVER, false, incompleteCode)

    override fun resolve(): PsiElement? = multiResolve(false).singleOrNull()?.element

    override fun isReferenceTo(target: PsiElement): Boolean =
        multiResolve(false).any { element.manager.areElementsEquivalent(it.element, target) }

    private fun findTargets(ids: Boolean): List<PsiElement> {
        val name = canonicalText
        val file = element.containingFile
        val targets =
            SkelligTestStepGetTargets
                .targets(
                    file,
                    ids,
                ).filter { SkelligTestStepGetTargets.targetName(it) == name }
                .toMutableList()
        if (DumbService.isDumb(element.project)) return targets
        val manager = PsiManager.getInstance(element.project)
        FileBasedIndex.getInstance().processValues(
            SkelligTestStepGetTargetIndex.INDEX_ID,
            SkelligTestStepGetTargetIndex.key(name, ids),
            null,
            { virtualFile, offsets ->
                ProgressManager.checkCanceled()
                val psiFile = manager.findFile(virtualFile)
                if (psiFile != null && psiFile != file) {
                    for (offset in offsets) {
                        val leaf = psiFile.findElementAt(offset) ?: continue
                        val target =
                            if (ids) {
                                PsiTreeUtil.getParentOfType(leaf, SkelligTestStepValue::class.java, false)
                            } else {
                                PsiTreeUtil.getParentOfType(leaf, SkelligTestStepKey::class.java, false)
                            }
                        if (target != null && SkelligTestStepGetTargets.targetName(target) == name) targets.add(target)
                    }
                }
                true
            },
            element.resolveScope,
        )
        return targets
    }

    override fun getVariants(): Array<Any> {
        val targets = mutableListOf<PsiElement>()
        val file = element.containingFile
        for (ids in listOf(true, false)) targets.addAll(SkelligTestStepGetTargets.targets(file, ids))
        if (!DumbService.isDumb(element.project)) {
            val index = FileBasedIndex.getInstance()
            val manager = PsiManager.getInstance(element.project)
            val files = mutableSetOf<VirtualFile>()
            val keys = mutableListOf<String>()
            index.processAllKeys(
                SkelligTestStepGetTargetIndex.INDEX_ID,
                { key ->
                    ProgressManager.checkCanceled()
                    keys.add(key)
                    true
                },
                element.resolveScope,
                null,
            )
            for (key in keys) {
                index.processValues(
                    SkelligTestStepGetTargetIndex.INDEX_ID,
                    key,
                    null,
                    { virtualFile, _ ->
                        files.add(virtualFile)
                        true
                    },
                    element.resolveScope,
                )
            }
            for (virtualFile in files) {
                ProgressManager.checkCanceled()
                val psiFile = manager.findFile(virtualFile) ?: continue
                if (psiFile != file) {
                    for (ids in listOf(true, false)) targets.addAll(SkelligTestStepGetTargets.targets(psiFile, ids))
                }
            }
        }
        return targets
            .distinctBy { SkelligTestStepGetTargets.targetName(it) }
            .mapNotNull { target ->
                SkelligTestStepGetTargets.targetName(target)?.let { name ->
                    LookupElementBuilder.create(name).withIcon(SkelligFileIcons.TEST_DATA_FILE).withTypeText(target.containingFile.name)
                }
            }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        SkelligTestStepGetTargets.renameLiteral(
            element.argList
                .first()
                .expressionList
                .single(),
            newElementName,
        )
        return element
    }

    companion object {
        private val RESOLVER =
            ResolveCache.PolyVariantResolver<SkelligTestStepGetReference> { reference, _ ->
                val ids = reference.findTargets(true)
                (ids.ifEmpty { reference.findTargets(false) }).map { PsiElementResolveResult(it) }.toTypedArray()
            }
    }
}
