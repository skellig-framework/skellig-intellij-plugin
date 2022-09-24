package org.skellig.plugin.language.feature.psi.formatter

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.skellig.plugin.language.feature.psi.SkelligLanguage

class SkelligCodeStylePanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings?) :
    TabbedLanguageCodeStylePanel(SkelligLanguage.INSTANCE, currentSettings, settings) {
    protected override fun initTabs(settings: CodeStyleSettings) {
        addIndentOptionsTab(settings)
    }
}