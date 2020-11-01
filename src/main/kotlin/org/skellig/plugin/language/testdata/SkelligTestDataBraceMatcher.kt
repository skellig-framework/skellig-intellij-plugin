package org.skellig.plugin.language.testdata

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.testdata.psi.SkelligTestDataTypes

class SkelligTestDataBraceMatcher : PairedBraceMatcher {

    private val PAIRS = arrayOf(
            BracePair(SkelligTestDataTypes.OBJECTOPENBRACKET, SkelligTestDataTypes.OBJECTCLOSEBRACKET, false),
            BracePair(SkelligTestDataTypes.ARRAYOPENBRACKET, SkelligTestDataTypes.ARRAYCLOSEBRACKET, false),
            BracePair(SkelligTestDataTypes.FUNCTIONOPENBRACKET, SkelligTestDataTypes.FUNCTIONCLOSEBRACKET, true))

    override fun getPairs(): Array<out @NotNull BracePair> {
        return PAIRS
    }

    override fun isPairedBracesAllowedBeforeType(type: IElementType, tokenType: IElementType?): Boolean {
        return false
    }

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }
}