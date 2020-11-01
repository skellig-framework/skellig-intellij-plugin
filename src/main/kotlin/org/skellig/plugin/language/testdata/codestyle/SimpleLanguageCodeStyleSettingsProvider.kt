package org.skellig.plugin.language.testdata.codestyle

import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.testdata.SkelligTestDataLanguage


class SimpleLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    @NotNull
    override fun getLanguage(): Language {
        return SkelligTestDataLanguage.INSTANCE
    }

    override fun customizeSettings(@NotNull consumer: CodeStyleSettingsCustomizable, @NotNull settingsType: SettingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
            consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Separator")
        } else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
        }
    }

    override fun getCodeSample(@NotNull settingsType: SettingsType): String {
        return """// This is a comment.
               name(execute command (.*)) {
                  variables {
                     args = "-a 1 -b 2"
                  }
                  
                  payload {
                     json [
                       {
                           a = $\{}
                           c = get(key)
                       }
                     ]
                  }
                  
                  response {
                     a = b
                     c = d
                  }
               }
                """
    }
}