package org.skellig.plugin.language.feature

import com.intellij.openapi.module.Module
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.search.TextOccurenceProcessor
import com.intellij.util.Processor
import org.jetbrains.annotations.NonNls
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import org.skellig.plugin.language.feature.steps.reference.SkelligExtensionPoint
import java.util.*
import java.util.regex.Pattern

object SkelligUtil {
    @NonNls
    val STEP_DEFINITIONS_DIR_NAME = "step_definitions"
    val ARR = arrayOf(
        arrayOf("\\\\", "\\\\\\\\"),
        arrayOf("\\|", "\\\\|"),
        arrayOf("\\$", "\\\\\\$"),
        arrayOf("\\^", "\\\\^"),
        arrayOf("\\+", "\\+"),
        arrayOf("\\-", "\\\\-"),
        arrayOf("\\#", "\\\\#"),
        arrayOf("\\?", "\\\\?"),
        arrayOf("\\*", "\\\\*"),
        arrayOf("\\/", "\\\\/"),
        arrayOf("\\{", "\\\\{"),
        arrayOf("\\}", "\\\\}"),
        arrayOf("\\[", "\\\\["),
        arrayOf("\\]", "\\\\]"),
        arrayOf("\\(", "\\\\("),
        arrayOf("\\)", "\\\\)"),
        arrayOf("\\+", "\\\\+"),
        arrayOf("\"([^\\\\\"]*)\"", "\"([^\"]*)\""),
        arrayOf("(?<=^|[ .,])\\d+[ ]", "(\\\\d+) "),
        arrayOf("(?<=^|[ .,])\\d+[,]", "(\\\\d+),"),
        arrayOf("(?<=^|[ .,])\\d+[.]", "(\\\\d+)."),
        arrayOf("(?<=^|[ .,])\\d+$", "(\\\\d+)"),
        arrayOf("\\.", "\\\\."),
        arrayOf("(<[^>]*>)", "(.*)")
    )
    const val PREFIX_CHAR = "^"
    const val SUFFIX_CHAR = "$"
    private val ESCAPE_PATTERN = Pattern.compile("([\\\\^\\[$.|?*+\\]])")
    private val OPTIONAL_PATTERN = Pattern.compile("(\\\\\\\\)?\\(([^)]+)\\)")
    var STANDARD_PARAMETER_TYPES: Map<String, String>? = null

    init {
        val standardParameterTypes = mapOf(
            kotlin.Pair("int", "-?\\d+"),
            kotlin.Pair("float", "-?\\d*[.,]?\\d+"),
            kotlin.Pair("word", "[^\\s]+"),
            kotlin.Pair("string", "\"(?:[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|'(?:[^'\\\\]*(?:\\\\.[^'\\\\]*)*)'"),
            kotlin.Pair("", "(.*)")
        )
        STANDARD_PARAMETER_TYPES = Collections.unmodifiableMap(standardParameterTypes)
    }

    fun getTheBiggestWordToSearchByIndex(regexp: String): String {
        var result = ""
        var start = 0
        if (regexp.startsWith(PREFIX_CHAR)) {
            start += PREFIX_CHAR.length
        }
        var end = regexp.length
        if (regexp.endsWith(SUFFIX_CHAR)) {
            end -= SUFFIX_CHAR.length
        }
        var sb: StringBuilder? = StringBuilder()
        for (i in start until end) {
            val c = regexp[i]
            if (sb != null && Character.isLetterOrDigit(c)) {
                sb.append(c)
            } else {
                if (Character.isWhitespace(c)) {
                    if (sb != null && sb.length > result.length) {
                        result = sb.toString()
                    }
                    sb = StringBuilder()
                } else {
                    sb = null
                }
            }
        }
        if (sb != null && sb.toString().length > result.length) {
            result = sb.toString()
        }
        return result
    }

    fun prepareStepRegexp(stepName: String): String {
        var result = stepName
        for (rule in ARR) {
            result = result.replace(rule[0].toRegex(), rule[1])
        }
        return result
    }

    /**
     * Processes unescaped slashes "/" (Alternative text) and pipes "|"
     * and not-necessary groups "(s)"in Cucumber Expressions.
     * From Cucumber's point of view the following code:
     * <pre>
     * `Then 'I print a word(s) red/blue using slash'...`
    </pre> *
     * converted into regexp:
     * <pre>
     * `I print a word(?:s)? (red|blue) using slash`
    </pre> *
     *
     * All pipes should be escaped.
     *
     * @see [Cucumber Expressions](https://docs.cucumber.io/cucumber/cucumber-expressions/)
     */
    fun processExpressionOrOperator(cucumberExpression: String): String {
        val result = StringBuilder()
        var i = 0
        var inGroup = false
        while (i < cucumberExpression.length) {
            val c = cucumberExpression[i]
            if (c == '/') {
                if (!inGroup) {
                    var j = result.length - 1
                    while (j >= 0 && !Character.isWhitespace(result[j])) {
                        j--
                    }
                    result.insert(j + 1, "(?:")
                    inGroup = true
                }
                result.append('|')
            } else if (c == '|') {
                result.append("\\|")
            } else {
                if (inGroup && Character.isWhitespace(c)) {
                    result.append(')')
                    inGroup = false
                }
                result.append(c)
            }
            i++
        }
        if (inGroup) {
            result.append(')')
        }
        return result.toString()
    }

    /**
     * Replaces ParameterType-s injected into step definition.
     * Step definition `provided {int} cucumbers ` will be presented by regexp `([+-]?\d+) customers `
     * @param parameterTypeManager provides mapping from ParameterTypes name to its value
     * @return regular expression defined by Cucumber Expression and ParameterTypes value
     */
    fun buildRegexpFromCucumberExpression(
        cucumberExpression: String,
        parameterTypeManager: ParameterTypeManager
    ): String {
        var expression = cucumberExpression
        expression = escapeCucumberExpression(expression)
        expression = replaceNotNecessaryTextTemplateByRegexp(expression)
        expression = processExpressionOrOperator(expression)
        val escapedCucumberExpression = expression
        val parameterTypeValues: MutableList<Pair<TextRange, String?>?> = ArrayList()
        processParameterTypesInCucumberExpression(escapedCucumberExpression) { range: TextRange ->
            val parameterTypeName = escapedCucumberExpression.substring(range.startOffset + 1, range.endOffset - 1)
            val parameterTypeValue = parameterTypeManager.getParameterTypeValue(parameterTypeName)
            parameterTypeValues.add(Pair.create(range, parameterTypeValue))
            true
        }
        val result = StringBuilder(escapedCucumberExpression)
        Collections.reverse(parameterTypeValues)
        for (rangeAndValue in parameterTypeValues) {
            val value = rangeAndValue!!.getSecond() ?: return escapedCucumberExpression
            val startOffset = rangeAndValue.first.startOffset
            val endOffset = rangeAndValue.first.endOffset
            result.replace(startOffset, endOffset, "($value)")
        }
        result.insert(0, '^')
        result.append('$')
        return result.toString()
    }

    /**
     * Replaces pattern (text) with regexp `(text)?`
     * For example Cucumber Expression:
     * `I have {int} cucumber(s) in my belly` is equal to regexp
     * `I have \d+ cucumber(?:s)? in my belly`
     */
    fun replaceNotNecessaryTextTemplateByRegexp(cucumberExpression: String): String {
        val matcher = OPTIONAL_PATTERN.matcher(cucumberExpression)
        val result = StringBuilder()
        while (matcher.find()) {
            val parameterPart = matcher.group(2)
            if ("\\\\" == matcher.group(1)) {
                matcher.appendReplacement(result, "\\\\($parameterPart\\\\)")
            } else {
                matcher.appendReplacement(result, "(?:$parameterPart)?")
            }
        }
        matcher.appendTail(result)
        return result.toString()
    }

    /**
     * Processes text ranges of every Parameter Type in Cucumber Expression
     */
    fun processParameterTypesInCucumberExpression(
        cucumberExpression: String,
        processor: Processor<in TextRange>
    ) {
        var i = 0
        while (i < cucumberExpression.length) {
            val c = cucumberExpression[i]
            if (c == '{') {
                var j = i
                while (j < cucumberExpression.length) {
                    val parameterTypeChar = cucumberExpression[j]
                    if (parameterTypeChar == '}') {
                        break
                    }
                    if (parameterTypeChar == '\\') {
                        j++
                    }
                    j++
                }
                if (j < cucumberExpression.length) {
                    processor.process(TextRange.create(i, j + 1))
                    i = j + 1
                    continue
                } else {
                    // unclosed parameter type
                    return
                }
            }
            if (c == '\\') {
                if (i >= cucumberExpression.length - 1) {
                    // escape without following symbol;
                    return
                }
                i++
            }
            i++
        }
    }

    /**
     * Step definition could be defined by regular expression or by Cucumber Expression (text with predefined patterns {int}, {float}, {word},
     * {string} or defined by user). This methods helps to distinguish these two cases
     */
    fun isCucumberExpression(stepDefinitionPattern: String): Boolean {
        if (stepDefinitionPattern.startsWith("^") && stepDefinitionPattern.endsWith("$")) {
            return false
        }
        val containsParameterTypes = booleanArrayOf(false)
        processParameterTypesInCucumberExpression(stepDefinitionPattern) { textRange: TextRange ->
            if (textRange.length < 2) {
                // at least "{}" expected here
                return@processParameterTypesInCucumberExpression true
            }
            val parameterTypeCandidate = stepDefinitionPattern.substring(textRange.startOffset + 1, textRange.endOffset - 1)
            if (!StringUtil.isNotNegativeNumber(parameterTypeCandidate) && !parameterTypeCandidate.contains(",")) {
                containsParameterTypes[0] = true
            }
            true
        }
        return containsParameterTypes[0]
    }

    /**
     * Substitutes scenario parameters into step. For example step from
     * Scenario
     * Given project with <count> participants
     * Example
     * | count |
     * | 10    |
     *
     * will be transformed to
     * Given project with 10 participants
     *
     * @param stepName
     * @param outlineTableMap mapping from header to the first data row
     * @return OutlineStepSubstitution that contains result step name and can calculate offsets
    </count> */
    fun substituteTableReferences(stepName: String, outlineTableMap: Map<String, String>?): OutlineStepSubstitution {
        if (outlineTableMap == null) {
            return OutlineStepSubstitution(stepName, emptyList())
        }
        val offsets: MutableList<Pair<Int, Int>> = ArrayList()
        val result = StringBuilder()
        var currentOffset = 0
        while (true) {
            val start = stepName.indexOf('<', currentOffset)
            if (start < 0) {
                break
            }
            val end = stepName.indexOf('>', start)
            if (end < 0) {
                break
            }
            val columnName = stepName.substring(start + 1, end)
            val value = outlineTableMap[columnName] ?: return OutlineStepSubstitution(stepName)
            result.append(stepName.subSequence(currentOffset, start))
            val replaceOffset = result.length
            result.append(value)
            val outlineParameterLength = end - start + 1
            val valueLength = value.length
            offsets.add(Pair(replaceOffset, outlineParameterLength - valueLength))
            currentOffset = end + 1
        }
        result.append(stepName.subSequence(currentOffset, stepName.length))
        return OutlineStepSubstitution(result.toString(), offsets)
    }

    fun escapeCucumberExpression(stepPattern: String): String {
        return ESCAPE_PATTERN.matcher(stepPattern).replaceAll("\\\\$1")
    }

    fun getLineNumber(element: PsiElement): Int? {
        val containingFile = element.containingFile
        val project = containingFile.project
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        val document = psiDocumentManager.getDocument(containingFile)
        val textOffset = element.textOffset
        return if (document == null) {
            null
        } else document.getLineNumber(textOffset) + 1
    }

    fun loadFrameworkSteps(framework: SkelligExtensionPoint, featureFile: PsiFile?, module: Module): List<AbstractStepDefinition> {
        return framework.loadStepsFor(featureFile, module)
    }

    /**
     * Accepts each element and checks if it has reference to some other element
     */
    private class MyReferenceCheckingProcessor private constructor(
        private val myElementToFind: PsiElement,
        private val myConsumer: Processor<in PsiReference>
    ) : TextOccurenceProcessor {
        override fun execute(element: PsiElement, offsetInElement: Int): Boolean {
            val parent = element.parent
            val result = executeInternal(element)
            // We check element and its parent (StringLiteral is probably child of SkelligFeatureStep that has reference)
            // TODO: Search for SkelligStep parent?
            return if (result && parent != null) {
                executeInternal(parent)
            } else result
        }

        /**
         * Gets all injected reference and checks if some of them points to [.myElementToFind]
         *
         * @param referenceOwner element with injected references
         * @return true if element found and consumed
         */
        private fun executeInternal(referenceOwner: PsiElement): Boolean {
            for (ref in referenceOwner.references) {
                if (ref != null && ref.isReferenceTo(myElementToFind)) {
                    if (!myConsumer.process(ref)) {
                        return false
                    }
                }
            }
            return true
        }
    }
}