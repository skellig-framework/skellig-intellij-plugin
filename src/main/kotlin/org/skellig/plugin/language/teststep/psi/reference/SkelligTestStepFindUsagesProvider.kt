package org.skellig.plugin.language.teststep.psi.reference

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepKey
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

class SkelligTestStepFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner =
        DefaultWordsScanner(
            SkelligTestStepLexerAdapter(),
            TokenSet.create(
                SkelligTestStepTypes.ID,
                SkelligTestStepTypes.NAME,
                SkelligTestStepTypes.VALUES,
                SkelligTestStepTypes.STATE,
                SkelligTestStepTypes.VALIDATE,
                SkelligTestStepTypes.WHERE,
                SkelligTestStepTypes.PAYLOAD,
                SkelligTestStepTypes.BODY,
                SkelligTestStepTypes.MESSAGE,
                SkelligTestStepTypes.REQUEST,
                SkelligTestStepTypes.INT,
            ),
            TokenSet.create(SkelligTestStepTypes.COMMENT),
            TokenSet.create(SkelligTestStepTypes.STRING),
        )

    override fun canFindUsagesFor(element: PsiElement): Boolean = SkelligTestStepGetTargets.targetName(element) != null

    override fun getHelpId(element: PsiElement): String? = null

    override fun getType(element: PsiElement): String = if (element is SkelligTestStepKey) "state key" else "test step ID"

    override fun getDescriptiveName(element: PsiElement): String = SkelligTestStepGetTargets.targetName(element) ?: element.text

    override fun getNodeText(
        element: PsiElement,
        useFullName: Boolean,
    ): String = getDescriptiveName(element)
}
