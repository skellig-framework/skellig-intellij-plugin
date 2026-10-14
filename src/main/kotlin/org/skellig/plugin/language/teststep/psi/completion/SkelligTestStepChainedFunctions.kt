package org.skellig.plugin.language.teststep.psi.completion

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.skellig.plugin.language.teststep.SkelligTestStepLexerAdapter
import org.skellig.plugin.language.teststep.psi.SkelligTestStepTypes

internal object SkelligTestStepChainedFunctions {
    // Built-ins registered by DefaultFunctionValueExecutor in skellig-test-step-processing.
    // Only executors that consume the receiver value are included (including inherited executors).
    val names =
        setOf(
            "add",
            "all",
            "any",
            "between",
            "contains",
            "count",
            "find",
            "findAll",
            "findLast",
            "first",
            "fromIndex",
            "fromRegex",
            "getValues",
            "groupBy",
            "jsonPath",
            "jsonToList",
            "jsonToMap",
            "map",
            "match",
            "maxOf",
            "minOf",
            "none",
            "size",
            "sort",
            "subString",
            "subStringLast",
            "sumOf",
            "toBigDecimal",
            "toBoolean",
            "toByte",
            "toBytes",
            "toDate",
            "toDateTime",
            "toDouble",
            "toFloat",
            "toInstant",
            "toInt",
            "toJson",
            "toLong",
            "toShort",
            "toString",
            "xpath",
        )

    fun isAfterDot(
        text: CharSequence,
        prefixStart: Int,
    ): Boolean {
        if (prefixStart <= 0) return false
        val lexer = SkelligTestStepLexerAdapter()
        lexer.start(text)
        var previous: IElementType? = null
        var last: IElementType? = null
        while (lexer.tokenType != null && lexer.tokenStart < prefixStart) {
            if (lexer.tokenEnd > prefixStart) return false
            if (lexer.tokenType != TokenType.WHITE_SPACE) {
                previous = last
                last = lexer.tokenType
            }
            lexer.advance()
        }
        return last == SkelligTestStepTypes.DOT && previous in receiverTokens
    }

    private val receiverTokens =
        setOf(
            SkelligTestStepTypes.ID,
            SkelligTestStepTypes.NAME,
            SkelligTestStepTypes.KEY_SYMBOLS,
            SkelligTestStepTypes.STRING,
            SkelligTestStepTypes.FUNCTION_R_BRACKET,
            SkelligTestStepTypes.OBJECT_R_BRACKET,
            SkelligTestStepTypes.ARRAY_R_BRACKET,
            SkelligTestStepTypes.VALUES,
            SkelligTestStepTypes.STATE,
            SkelligTestStepTypes.VALIDATE,
            SkelligTestStepTypes.WHERE,
            SkelligTestStepTypes.PAYLOAD,
            SkelligTestStepTypes.BODY,
            SkelligTestStepTypes.MESSAGE,
            SkelligTestStepTypes.REQUEST,
        )
}
