package org.skellig.plugin.language.feature.psi.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider

class SkelligCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun createConfigurable(settings: CodeStyleSettings, modelSettings: CodeStyleSettings): CodeStyleConfigurable {
        return object : CodeStyleAbstractConfigurable(settings, modelSettings, this.configurableDisplayName) {
            protected override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return SkelligCodeStylePanel(currentSettings, settings)
            }
        }
    }

    override fun getConfigurableDisplayName(): String {
        return "Skellig Feature"
    }
}