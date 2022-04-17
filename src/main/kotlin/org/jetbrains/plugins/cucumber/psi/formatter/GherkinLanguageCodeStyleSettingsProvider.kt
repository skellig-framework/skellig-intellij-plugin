package org.jetbrains.plugins.cucumber.psi.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.jetbrains.plugins.cucumber.psi.SkelligLanguage

/**
 * @author Rustam Vishnyakov
 */
class GherkinLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions
    ) {
        indentOptions.INDENT_SIZE = 2
    }

    override fun getLanguage(): Language = SkelligLanguage.INSTANCE

    override fun getCodeSample(settingsType: SettingsType): String? {
        return """Feature: Formatting
    Scenario: Reformat Cucumber file
"""
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()
}