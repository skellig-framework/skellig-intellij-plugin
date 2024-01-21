package org.skellig.plugin.language.teststep

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.teststep.psi.*
import org.skellig.plugin.language.teststep.psi.impl.SkelligTestStepTestStepNameImpl
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToFeatureReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToStateReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToValuesAndPropertiesReference
import javax.swing.Icon

object SkelligTestStepPsiImplUtil {

    @JvmStatic
    fun getName(element: SkelligTestStepTestStepName): String {
        return if(element.text.length > 2) element.text.substring(1, element.text.length - 1) else ""
    }

    @JvmStatic
    fun setName(element: SkelligTestStepTestStepName, newName: String): PsiElement {
        return element
    }

    @JvmStatic
    fun getNameIdentifier(element: SkelligTestStepTestStepName): PsiElement? {
        return element.node.findChildByType(SkelligTestStepTypes.TEST_STEP_NAME)?.psi
    }

    @JvmStatic
    fun createTestStep(project: Project, name: String): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, name)
        return file.firstChild as SkelligTestStepTestStepName
    }

    @JvmStatic
    fun createFile(project: Project, text: String): SkelligTestStepFile {
        val name = "test-steps-new.sts"
        return PsiFileFactory.getInstance(project).createFileFromText(name, SkelligTestStepFileType.INSTANCE, text) as SkelligTestStepFile
    }

    @JvmStatic
    fun createTestStep(project: Project, name: String, value: String): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, "$name = $value")
        return file.firstChild as SkelligTestStepTestStepName
    }

    @JvmStatic
    fun getReference(element: SkelligTestStepReferenceKey): PsiReference {
        return SkelligTestStepToValuesAndPropertiesReference(element)
    }

    @JvmStatic
    fun getReference(element: SkelligTestStepFunctionExpression): PsiReference {
        return SkelligTestStepToStateReference(element)
    }

    @JvmStatic
    fun getReference(element: SkelligTestStepTestStepName): PsiReference {
        return SkelligTestStepToFeatureReference(element)
    }

    @JvmStatic
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

    @JvmStatic
    fun getPresentation(element: SkelligTestStepTestStepName): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return element.name
            }

            override fun getLocationString(): String {
                return element.containingFile.name
            }

            override fun getIcon(unused: Boolean): Icon {
                return SkelligFileIcons.TEST_DATA_FILE
            }
        }
    }

    @JvmStatic
    fun getPresentation(element: SkelligTestStepTestStepNameExpression): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return element.testStepName.name
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
