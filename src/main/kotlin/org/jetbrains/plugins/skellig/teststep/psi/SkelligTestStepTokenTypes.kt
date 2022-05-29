package org.jetbrains.plugins.skellig.teststep.psi

import com.intellij.psi.tree.TokenSet

interface SkelligTestStepTokenTypes {
    companion object {
        val COMMENT = SkelligTestStepElementType("COMMENT")
        val TEXT = SkelligTestStepElementType("TEXT")
        val EQUAL = SkelligTestStepElementType("EQUAL")
        val OBJECT_OPEN_BRACKET = SkelligTestStepElementType("OBJECT_OPEN_BRACKET")  // {
        val OBJECT_CLOSE_BRACKET = SkelligTestStepElementType("OBJECT_CLOSE_BRACKET")  // }
        val ARRAY_OPEN_BRACKET = SkelligTestStepElementType("ARRAY_OPEN_BRACKET")  // [
        val ARRAY_CLOSE_BRACKET = SkelligTestStepElementType("ARRAY_CLOSE_BRACKET")  // ]
        val OPEN_BRACKET = SkelligTestStepElementType("OPEN_BRACKET")  // (
        val CLOSE_BRACKET = SkelligTestStepElementType("CLOSE_BRACKET")  // )
        val PARAMETER = SkelligTestStepElementType("PARAMETER_BRACKET")  // ${
        val EXPRESSION = SkelligTestStepElementType("EXPRESSION")  // #[
        val FUNCTION = SkelligTestStepElementType("FUNCTION")  // ( )
        val COLON = SkelligTestStepElementType("COLON")
        val DOT = SkelligTestStepElementType("DOT")
        val NAME = SkelligTestStepElementType("NAME")
        val ID = SkelligTestStepElementType("ID")
        val IF = SkelligTestStepElementType("IF")
        val TEMPLATE = SkelligTestStepElementType("TEMPLATE")
        val JSON = SkelligTestStepElementType("JSON")
        val CSV = SkelligTestStepElementType("CSV")
        val VARIABLES = SkelligTestStepElementType("VARIABLES")
        val MESSAGE = SkelligTestStepElementType("MESSAGE")
        val REQUEST = SkelligTestStepElementType("REQUEST")
        val BODY = SkelligTestStepElementType("BODY")
        val PAYLOAD = SkelligTestStepElementType("PAYLOAD")
        val VALIDATE = SkelligTestStepElementType("VALIDATE")
        val ASSERT = SkelligTestStepElementType("ASSERT")
        val FROMTEST = SkelligTestStepElementType("FROMTEST")
        val STRING_TEXT = SkelligTestStepElementType("STRING_TEXT")

        val KEYWORDS = TokenSet.create(
            NAME, ID, IF, TEMPLATE, JSON, CSV, VARIABLES, MESSAGE, REQUEST, BODY, PAYLOAD, VALIDATE, ASSERT, FROMTEST
        )

        val REQUESTS = TokenSet.create(
            MESSAGE, REQUEST, BODY, PAYLOAD
        )

        val VALIDATIONS = TokenSet.create(
            VALIDATE, ASSERT
        )

        val SPECIAL_SYMBOLS = TokenSet.create(
            OBJECT_OPEN_BRACKET, OBJECT_CLOSE_BRACKET, ARRAY_OPEN_BRACKET, ARRAY_CLOSE_BRACKET, EQUAL, COLON, DOT
        )

        val NOT_FIELD_SPECIAL_SYMBOLS = TokenSet.create(
            OBJECT_OPEN_BRACKET, ARRAY_OPEN_BRACKET, EQUAL, COLON
        )

        val VALUE_TOKENS = TokenSet.create(
            TEXT, PARAMETER, FUNCTION, EXPRESSION, DOT
        )
    }
}