// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.steps

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import org.jetbrains.annotations.Contract
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

abstract class AbstractStepDefinition(element: PsiElement) {
    private val myElementPointer: SmartPsiElementPointer<PsiElement>

    @Volatile
    private var myRegexText: String? = null

    @Volatile
    private var myRegex: Pattern? = null

    abstract val variableNames: List<String?>?

    fun matches(stepName: String): Boolean {
        val pattern = getPattern() ?: return false
        val stepChars = StringUtil.newBombedCharSequence(stepName, TIME_TO_CHECK_STEP_BY_REGEXP_MILLIS.toLong())
        return try {
            pattern.matcher(stepChars).find()
        } catch (ignore: ProcessCanceledException) {
            false
        }
    }

    open fun getElement(): PsiElement? {
        return myElementPointer.element
    }

    open fun getCucumberRegex(): String? {
        return getExpression()
    }

    open fun getExpression(): String? {
        return getCucumberRegexFromElement(getElement());
    }

    /**
     * @return regexp pattern for step or null if regexp is malformed
     */
    open fun getPattern(): Pattern? {
       return try {
            val cucumberRegex = getCucumberRegex() ?: return null
            if (myRegexText == null || cucumberRegex != myRegexText) {
                val patternText = StringBuilder(ESCAPE_PATTERN.matcher(cucumberRegex).replaceAll("(.*)"))
                if (patternText.toString().startsWith(CUCUMBER_START_PREFIX)) {
                    patternText.replace(0, CUCUMBER_START_PREFIX.length, "^")
                }
                if (patternText.toString().endsWith(CUCUMBER_END_SUFFIX)) {
                    patternText.replace(patternText.length - CUCUMBER_END_SUFFIX.length, patternText.length, "$")
                }
                myRegex = Pattern.compile(patternText.toString(), if (isCaseSensitive) 0 else Pattern.CASE_INSENSITIVE)
                myRegexText = cucumberRegex
            }
            myRegex
        } catch (ignored: PatternSyntaxException) {
            null // Bad regex?
        }
    }

    @Contract("null -> null")
    protected abstract fun getCucumberRegexFromElement(element: PsiElement?): String?

    protected val isCaseSensitive: Boolean
        protected get() = true

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as AbstractStepDefinition
        return if (myElementPointer != that.myElementPointer) false else true
    }

    override fun hashCode(): Int {
        return myElementPointer.hashCode()
    }

    /**
     * Set new value for step definitions (most likely provided by refactor->rename)
     * @param newValue
     */
    fun setCucumberRegex(newValue: String) {}

    /**
     * Checks if step definitions point supports certain step (i.e. some step definitions does not support some keywords)
     *
     * @param step Step to check
     * @return true if supports.
     */
    fun supportsStep(step: PsiElement): Boolean {
        return true
    }

    /**
     * Checks if step definition supports rename.
     * @param newName if null -- check if definition supports renaming at all (regardless new name).
     * If not null -- check if it can be renamed to the new (provided) name.
     * @return true if rename is supported
     */
    fun supportsRename(newName: String?): Boolean {
        return true
    }

    companion object {
        private val ESCAPE_PATTERN = Pattern.compile("(#\\{.+?})")
        private const val CUCUMBER_START_PREFIX = "\\A"
        private const val CUCUMBER_END_SUFFIX = "\\z"
        private const val TIME_TO_CHECK_STEP_BY_REGEXP_MILLIS = 300
    }

    init {
        myElementPointer = SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)
    }
}