package org.jetbrains.plugins.cucumber.psi.formatter

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.plugins.cucumber.psi.SkelligLanguage

class GherkinCodeStylePanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings?) :
    TabbedLanguageCodeStylePanel(SkelligLanguage.INSTANCE, currentSettings, settings) {
    protected override fun initTabs(settings: CodeStyleSettings) {
        addIndentOptionsTab(settings)
    }
}