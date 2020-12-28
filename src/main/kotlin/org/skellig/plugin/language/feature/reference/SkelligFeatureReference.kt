package org.skellig.plugin.language.feature.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.feature.psi.SkelligFeatureStep
import org.skellig.plugin.language.feature.psi.SkelligFeatureStepsUtil
import java.util.*


class SkelligFeatureReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {

    private var name: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project: Project = myElement.project
        val properties: Collection<SkelligFeatureStep> = SkelligFeatureStepsUtil.findSteps(project, name)
        val results: MutableList<ResolveResult> = ArrayList<ResolveResult>()
        for (property in properties) {
            results.add(PsiElementResolveResult(property))
        }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults: Array<ResolveResult> = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].getElement() else null
    }

    override fun getVariants(): Array<Any> {
        val project: Project = myElement.project
        val properties: Collection<SkelligFeatureStep> = SkelligFeatureStepsUtil.findSteps(project)
        val variants: MutableList<LookupElement> = ArrayList()
        for (property in properties) {
            if (property.getName() != null /*&& property.getname().length() > 0*/) {
                variants.add(LookupElementBuilder
                        .create(property).withIcon(SkelligFileIcons.FEATURE_FILE)
                        .withTypeText(property.getContainingFile().getName())
                )
            }
        }
        return variants.toTypedArray()
    }
}