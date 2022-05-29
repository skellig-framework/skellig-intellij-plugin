// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

interface SkelligTestStepElementTypes {
    companion object {
        val FILE = IFileElementType(SkelligTestStepLanguage.INSTANCE)
        val STEP: IElementType = SkelligTestStepElementType("Name")
        val ID = SkelligTestStepElementType("id")
        val VARIABLES = SkelligTestStepElementType("variables")
        val TEXT = SkelligTestStepElementType("text")
        val VALUE = SkelligTestStepElementType("value")  // what goes after '='. Is a combination of TEXT, PARAM, FUNCTION, EXPRESSION
        val OBJECT = SkelligTestStepElementType("object")
        val ARRAY = SkelligTestStepElementType("array")
        val FIELD_VALUE = SkelligTestStepElementType("FIELD_VALUE")
        val PARAMETER = SkelligTestStepElementType("parameter")
        val EXPRESSION = SkelligTestStepElementType("expression")
        val FUNCTION = SkelligTestStepElementType("function")
        val DOT = SkelligTestStepElementType("dot")
        val KEYWORD = SkelligTestStepElementType("keyword")

        val IF = SkelligTestStepElementType("if")
        val REQUEST = SkelligTestStepElementType("request")
        val VALIDATION = SkelligTestStepElementType("validate")

        val elementsForToken = mapOf(
            Pair(SkelligTestStepTokenTypes.TEXT, TEXT),
            Pair(SkelligTestStepTokenTypes.DOT, DOT),

            Pair(SkelligTestStepTokenTypes.VALIDATE, VALIDATION),
            Pair(SkelligTestStepTokenTypes.ASSERT, VALIDATION),

            Pair(SkelligTestStepTokenTypes.REQUEST, REQUEST),
            Pair(SkelligTestStepTokenTypes.MESSAGE, REQUEST),
            Pair(SkelligTestStepTokenTypes.PAYLOAD, REQUEST),
            Pair(SkelligTestStepTokenTypes.BODY, REQUEST),

            Pair(SkelligTestStepTokenTypes.IF, KEYWORD),
            Pair(SkelligTestStepTokenTypes.TEMPLATE, KEYWORD),
            Pair(SkelligTestStepTokenTypes.JSON, KEYWORD),
            Pair(SkelligTestStepTokenTypes.CSV, KEYWORD),
            Pair(SkelligTestStepTokenTypes.IF, KEYWORD),
        )

        val elementsInArrayForToken = mapOf(
            Pair(SkelligTestStepTokenTypes.TEXT, TEXT),
            Pair(SkelligTestStepTokenTypes.DOT, DOT),
            Pair(SkelligTestStepTokenTypes.PARAMETER, PARAMETER),
            Pair(SkelligTestStepTokenTypes.EXPRESSION, EXPRESSION),
            Pair(SkelligTestStepTokenTypes.FUNCTION, FUNCTION),
        )
    }
}