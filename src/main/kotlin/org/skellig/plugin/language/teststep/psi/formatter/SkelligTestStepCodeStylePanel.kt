package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.skellig.plugin.language.teststep.psi.SkelligTestStepLanguage

class SkelligTestStepCodeStylePanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings) :
    TabbedLanguageCodeStylePanel(SkelligTestStepLanguage.INSTANCE, currentSettings, settings) {

    override fun initTabs(settings: CodeStyleSettings) {
        addIndentOptionsTab(settings)
    }
}