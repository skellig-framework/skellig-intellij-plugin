package org.jetbrains.plugins.cucumber.psi.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider

class GherkinCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings): Configurable {
        return object : CodeStyleAbstractConfigurable(settings, originalSettings, this.configurableDisplayName) {
            protected override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return GherkinCodeStylePanel(currentSettings, settings)
            }
        }
    }

    override fun getConfigurableDisplayName(): String? {
        return "Skellig Feature"
    }
}