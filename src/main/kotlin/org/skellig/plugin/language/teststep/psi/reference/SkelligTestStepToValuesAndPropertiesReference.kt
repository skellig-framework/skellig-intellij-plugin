package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.apache.commons.lang.StringUtils
import org.jetbrains.plugins.hocon.lang.HoconFileType
import org.jetbrains.plugins.hocon.psi.HFieldKey
import org.jetbrains.plugins.hocon.psi.HoconPsiFile
import org.skellig.plugin.language.teststep.psi.SkelligTestStepPair
import org.skellig.plugin.language.teststep.psi.SkelligTestStepReferenceKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import java.util.regex.Pattern


private const val DOUBLE_QUOTE = "\""
private const val DOT = "."

class SkelligTestStepToValuesAndPropertiesReference(stepParameter: SkelligTestStepReferenceKey) : SkelligTestStepSimpleReference(stepParameter) {
    val element: SkelligTestStepReferenceKey
        get() = super.getElement() as SkelligTestStepReferenceKey

    override fun resolve(): PsiElement? {
        val parameterText = element.text
        if (StringUtils.isNumeric(parameterText)) {
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStepTestStepNameExpression::class.java) ?: return null
            val groupPattern = Pattern.compile("\\(.*\\)")
            val matcher = groupPattern.matcher(testStep.testStepName.text)
            if (matcher.find()) {
                return testStep
            }
        } else {
            var foundItem: PsiElement? = null
            val testStep = PsiTreeUtil.getParentOfType(element, SkelligTestStepTestStepNameExpression::class.java) ?: return null
            val pairs = PsiTreeUtil.getChildrenOfTypeAsList(testStep, SkelligTestStepPair::class.java)
                .find { it.key.text == "values" }
            pairs?.let { values ->
                values.map?.let {
                    for (pair in it.pairList) {
                        val text = pair.key.text
                        if (text.startsWith(parameterText)) {
                            foundItem = pair
                            break
                        }
                    }
                }
            }

            if (foundItem == null) {
                val module = ModuleUtilCore.findModuleForPsiElement(element)
                foundItem = module?.let { findReferenceToConfigProperty(it, parameterText) }
            }
            return foundItem
        }
        return null
    }

    private fun findReferenceToConfigProperty(module: com.intellij.openapi.module.Module, keyToSearch: String): PsiElement? {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val project = module.project

        val elements = mutableListOf<PsiElement?>()
        val psiManager = PsiManager.getInstance(project)

        val keysToSearch =
            if (keyToSearch.startsWith(DOUBLE_QUOTE) && keyToSearch.endsWith(DOUBLE_QUOTE))
                keyToSearch.substring(1, keyToSearch.length - 1).split(DOT)
            else keyToSearch.split(DOT)

        fileBasedIndex.iterateIndexableFiles({ virtualFile: VirtualFile ->
            if (elements.isEmpty() && !virtualFile.isDirectory && virtualFile.extension == HoconFileType.DefaultExtension()) {
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile != null && psiFile is HoconPsiFile) {
                    run breaking@{
                        psiFile.children.forEach { entries ->
                            var parentKey: PsiElement? = entries
                            keysToSearch.forEach { keyToSearch ->
                                parentKey = findNextKey(parentKey?.parent, keyToSearch)
                            }
                            if (parentKey != null && parentKey != entries) {
                                elements.add(parentKey)
                                return@breaking
                            }
                        }
                    }
                }
            }
            true
        }, project, null)

        return if (elements.isNotEmpty()) elements.first() else null
    }

    private fun findNextKey(parentElement: PsiElement?, keyToSearch: String): PsiElement? {
        var foundElement: PsiElement? = null
        if (parentElement is HFieldKey) {
            if (parentElement.text == keyToSearch) foundElement = parentElement
        } else {
            run breaking@{
                parentElement?.children?.forEach {
                    findNextKey(it, keyToSearch)?.let { e ->
                        foundElement = e
                        return@breaking
                    }
                }
            }
        }
        return foundElement
    }
}

open class SkelligTestStepSimpleReference(private val myElement: PsiElement) : PsiReference {
    override fun getElement(): PsiElement {
        return myElement
    }

    override fun getRangeInElement(): TextRange {
        return TextRange(0, myElement.textLength)
    }

    override fun resolve(): PsiElement? {
        return myElement
    }

    override fun getCanonicalText(): String {
        return myElement.text
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        if (myElement is PsiNamedElement) {
            myElement.setName(newElementName)
        }
        return myElement
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        return myElement
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val myResolved = resolve()
        var resolved = if (element.reference != null) element.reference!!.resolve() else null
        if (resolved == null) {
            resolved = element
        }
        return myResolved != null && resolved == myResolved
    }

    override fun isSoft(): Boolean {
        return false
    }
}