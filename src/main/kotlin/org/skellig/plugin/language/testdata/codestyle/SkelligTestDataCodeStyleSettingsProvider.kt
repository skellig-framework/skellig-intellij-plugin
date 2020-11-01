package org.skellig.plugin.language.testdata.codestyle

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage


class SkelligTestDataCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun createCustomSettings(settings: CodeStyleSettings?): CustomCodeStyleSettings? {
        return SkelligTestDataCodeStyleSettings(settings)
    }

    @Nullable
    override fun getConfigurableDisplayName(): String? {
        return "Skellig Test Data"
    }

    @NotNull
    override fun createConfigurable(@NotNull settings: CodeStyleSettings, @NotNull modelSettings: CodeStyleSettings): CodeStyleConfigurable {
        return object : CodeStyleAbstractConfigurable(settings, modelSettings, this.configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return SimpleCodeStyleMainPanel(currentSettings, settings)
            }
        }
    }

    private class SimpleCodeStyleMainPanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings?) :
            TabbedLanguageCodeStylePanel(SkelligTestDataLanguage.INSTANCE, currentSettings, settings)
}