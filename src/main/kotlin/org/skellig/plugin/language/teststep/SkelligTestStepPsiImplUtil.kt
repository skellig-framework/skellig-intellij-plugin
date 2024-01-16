// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.skellig.plugin.language.teststep

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.teststep.psi.*
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepRefReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToValuesAndStateReference
import javax.swing.Icon

object SkelligTestStepPsiImplUtil {
    fun getName(element: SkelligTestStepTestStepName): String {
        return element.text
    }

    fun getName(element: SkelligTestStepTestStepName?): String {
        val keyNode = element!!.node.findChildByType(SkelligTestStepTypes.TEST_STEP_NAME)
        return keyNode?.let { keyNode.text.replace("\\\\".toRegex(), " ") } ?: ""
    }

    fun setName(element: SkelligTestStepTestStepName, newName: String): PsiElement {
        val keyNode = element.node.findChildByType(SkelligTestStepTypes.TEST_STEP_NAME)
        keyNode?.let {
            val property: SkelligTestStepTestStepName = createTestStep(element.getProject(), newName)
            val newKeyNode: ASTNode = property.firstChild.node
            element.node.replaceChild(keyNode, newKeyNode)
        }
        return element
    }

    fun getNameIdentifier(element: SkelligTestStepTestStepName): PsiElement? {
        return element.node.findChildByType(SkelligTestStepTypes.TEST_STEP_NAME)?.psi
    }

    fun createTestStep(project: Project, name: String): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, name)
        return file.firstChild as SkelligTestStepTestStepName
    }

    fun createFile(project: Project, text: String): SkelligTestStepFile {
        val name = "test-steps-new.sts"
        return PsiFileFactory.getInstance(project).createFileFromText(name, SkelligTestStepFileType.INSTANCE, text) as SkelligTestStepFile
    }

    fun createTestStep(project: Project, name: String, value: String): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, "$name = $value")
        return file.firstChild as SkelligTestStepTestStepName
    }

    fun getReference(element: SkelligTestStepReferenceKey): PsiReference {
        return SkelligTestStepToValuesAndStateReference(element)
    }

    fun getReference(element: SkelligTestStepFunctionExpression): PsiReference {
        return SkelligTestStepRefReference(element)
    }

    fun getPresentation(element: SkelligTestStepKey): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.text
            }

            override fun getLocationString(): String {
                return element.containingFile.name
            }

            override fun getIcon(unused: Boolean): Icon {
                return SkelligFileIcons.TEST_DATA_FILE
            }
        }
    }
}
