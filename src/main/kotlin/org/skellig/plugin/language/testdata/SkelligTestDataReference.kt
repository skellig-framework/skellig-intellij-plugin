package org.skellig.plugin.language.testdata

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.jetbrains.annotations.NotNull
import java.util.*


class SkelligTestDataReference(@NotNull element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {

    private val key: String? = null

    override fun resolve(): PsiElement? {
        val resolveResults = arrayOf(multiResolve(false));
        return if (resolveResults.size == 1) {
            resolveResults[0] as PsiElement
        } else {
            null
        }
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project: Project = myElement.project
//        val properties = SkelligTestDataUtil.findProperties(project, key!!)
        val results: MutableList<ResolveResult> = ArrayList()
       /* for (property in properties) {
            results.add(PsiElementResolveResult(property!!))
        }*/
        return results.toTypedArray()
    }

    override fun getVariants(): Array<Any> {
        val project: Project = myElement.project
//        val properties = SkelligTestDataUtil.findProperties(project)
        val variants: MutableList<LookupElement> = ArrayList()
       /* for (property in properties) {
            if (property!!.key != null && property.key.length > 0) {
                variants.add(LookupElementBuilder
                        .create(property).withIcon(SkelligFileIcons.FILE)
                        .withTypeText(property.containingFile.name)
                )
            }
        }*/
        return variants.toTypedArray()
    }
}