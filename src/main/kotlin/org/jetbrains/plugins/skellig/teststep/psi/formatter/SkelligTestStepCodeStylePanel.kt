package org.jetbrains.plugins.skellig.teststep.psi.formatter

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.jetbrains.plugins.cucumber.psi.SkelligLanguage
import org.jetbrains.plugins.skellig.teststep.psi.SkelligTestStepLanguage

class SkelligTestStepCodeStylePanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings?) :
    TabbedLanguageCodeStylePanel(SkelligTestStepLanguage.INSTANCE, currentSettings, settings) {

    override fun initTabs(settings: CodeStyleSettings) {
        addIndentOptionsTab(settings)
    }
}