package org.jetbrains.plugins.skellig.teststep.psi.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepLanguage

class SkelligTestStepLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions
    ) {
        indentOptions.INDENT_SIZE = 2
    }

    override fun getLanguage(): Language = SkelligTestStepLanguage.INSTANCE

    override fun getCodeSample(settingsType: SettingsType): String {
        return """name(test sample) {
                url = a/b/c
                request {
                   json {
                     field_1 = 1234
                   }
                }
                
                validate {
                   code = 201
                }
            }
"""
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()
}