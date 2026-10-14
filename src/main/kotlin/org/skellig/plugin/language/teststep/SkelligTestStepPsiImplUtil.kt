package org.skellig.plugin.language.teststep

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.feature.psi.SkelligUtil
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFile
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFileType
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepReferenceKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepName
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTestStepNameExpression
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepGetReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepGetTargets
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToFeatureReference
import org.skellig.plugin.language.teststep.psi.reference.SkelligTestStepToValuesAndPropertiesReference
import javax.swing.Icon

object SkelligTestStepPsiImplUtil {
    @JvmStatic
    fun getName(element: SkelligTestStepTestStepName): String =
        if (element.text.length > 2) element.text.substring(1, element.text.length - 1) else ""

    @JvmStatic
    fun setName(
        element: SkelligTestStepTestStepName,
        newName: String,
    ): PsiElement = element

    @JvmStatic
    fun getNameIdentifier(element: SkelligTestStepTestStepName): PsiElement? =
        element.node.findChildByType(SkelligTestStepTypes.TEST_STEP_NAME)?.psi

    @JvmStatic
    fun createTestStep(
        project: Project,
        name: String,
    ): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, name)
        return file.firstChild as SkelligTestStepTestStepName
    }

    @JvmStatic
    fun createFile(
        project: Project,
        text: String,
    ): SkelligTestStepFile {
        val name = "test-steps-new.sts"
        return PsiFileFactory.getInstance(project).createFileFromText(name, SkelligTestStepFileType.INSTANCE, text) as SkelligTestStepFile
    }

    @JvmStatic
    fun createTestStep(
        project: Project,
        name: String,
        value: String,
    ): SkelligTestStepTestStepName {
        val file: SkelligTestStepFile = createFile(project, "$name = $value")
        return file.firstChild as SkelligTestStepTestStepName
    }

    @JvmStatic
    fun getReference(element: SkelligTestStepReferenceKey): PsiReference = SkelligTestStepToValuesAndPropertiesReference(element)

    @JvmStatic
    fun getReference(element: SkelligTestStepFunctionExpression): PsiReference? =
        if (element.functionName.text == "get" &&
            element.argList.firstOrNull()?.let { SkelligTestStepGetTargets.literalName(it) } != null
        ) {
            SkelligTestStepGetReference(element)
        } else {
            null
        }

    @JvmStatic
    fun getReference(element: SkelligTestStepTestStepName): PsiReference {
        val testStepDef = SkelligUtil.getTestStepName(element)
        val textRange = if (testStepDef.isNotEmpty()) TextRange(1, testStepDef.length + 1) else TextRange(0, 0)
        return SkelligTestStepToFeatureReference(element, textRange)
    }

    @JvmStatic
    fun getPresentation(element: SkelligTestStepKey): ItemPresentation =
        object : ItemPresentation {
            override fun getPresentableText(): String? = element.text

            override fun getLocationString(): String = element.containingFile.name

            override fun getIcon(unused: Boolean): Icon = SkelligFileIcons.TEST_DATA_FILE
        }

    @JvmStatic
    fun getPresentation(element: SkelligTestStepTestStepName): ItemPresentation =
        object : ItemPresentation {
            override fun getPresentableText(): String = element.name ?: ""

            override fun getLocationString(): String = element.containingFile.name

            override fun getIcon(unused: Boolean): Icon = SkelligFileIcons.TEST_DATA_FILE
        }

    @JvmStatic
    fun getPresentation(element: SkelligTestStepTestStepNameExpression): ItemPresentation =
        object : ItemPresentation {
            override fun getPresentableText(): String = element.testStepName.name ?: ""

            override fun getLocationString(): String = element.containingFile.name

            override fun getIcon(unused: Boolean): Icon = SkelligFileIcons.TEST_DATA_FILE
        }
}
