package org.skellig.plugin.language.feature.codestyle

import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.jetbrains.annotations.NotNull
import org.skellig.plugin.language.feature.SkelligFeatureLanguage


class SkelligFeatureLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    @NotNull
    override fun getLanguage(): Language {
        return SkelligFeatureLanguage.INSTANCE
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
        return """//comment
@Tag1
Name: "Tests feature"

   @Tag2
   @Tag1_2 @Tag3
   Test: Tests scenario
      Given something
      Run "<name>" something <f-d>
      "Validate something"
       | k1    |v2 | v3| v4|
      Log result
       | k1    |v2 |
       | <k_1> |"v3."|

   @Tag3
   Test: Another test scenario
      Given value is <value>,
      Run function with <value>
      Validate result is <expected>
   Data:
      |value|expected|
      | v1  | <e_1>  |
      | v2  | e2     |
                """
    }
}