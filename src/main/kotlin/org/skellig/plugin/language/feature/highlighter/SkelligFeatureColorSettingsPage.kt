package org.skellig.plugin.language.feature.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligFeatureColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Feature Keywords", SkelligFeatureSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Tag", SkelligFeatureSyntaxHighlighter.TAG),
            AttributesDescriptor("Text", SkelligFeatureSyntaxHighlighter.FEATURE_TEXT),
            AttributesDescriptor("Parameter", SkelligFeatureSyntaxHighlighter.FEATURE_PARAMETER),
            AttributesDescriptor("Parameter Separator", SkelligFeatureSyntaxHighlighter.PARAMETER_SEPARATOR),
            AttributesDescriptor("String", SkelligFeatureSyntaxHighlighter.STRING),
            AttributesDescriptor("Comment", SkelligFeatureSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Bad Value", SkelligFeatureSyntaxHighlighter.SKELLIG_BAD_CHARACTER)
    )

    @Nullable
    override fun getIcon(): Icon? {
        return SkelligFileIcons.FEATURE_FILE
    }

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return SkelligFeatureSyntaxHighlighter()
    }

    @NotNull
    override fun getDemoText(): String {
        return """//comment
@Tag1  @Tag2
Name: Tests feature

Test: Tests scenario
  Given something
  Run <name> something <fd>
  Validate something
  | v1 |v2 | v3  | v4 |
  Log result
  | p1 |p2 |
  | v1 |v2 |

@Tag3
Test: Another test scenario
  Given value is <value>
  Run function with <value>
  Validate result is <expected>
Data:
  |value|expected|
  | v1  | v2     |
  | v3  | v4     |
                """
    }

    @Nullable
    override fun getAdditionalHighlightingTagToDescriptorMap(): @Nullable MutableMap<String, TextAttributesKey>? {
        return null
    }

    @NotNull
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    @NotNull
    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    @NotNull
    override fun getDisplayName(): String {
        return "Skellig Feature"
    }
}