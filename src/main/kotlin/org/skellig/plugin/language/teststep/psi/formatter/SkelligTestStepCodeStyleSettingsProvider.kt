package org.skellig.plugin.language.teststep.psi.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider

class SkelligTestStepCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun createConfigurable(settings: CodeStyleSettings, modelSettings: CodeStyleSettings): CodeStyleConfigurable {
        return object : CodeStyleAbstractConfigurable(settings, modelSettings, this.configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return SkelligTestStepCodeStylePanel(currentSettings, settings)
            }
        }
    }

    override fun getConfigurableDisplayName(): String {
        return "Skellig Test Step"
    }
}