package org.skellig.plugin.language.feature.psi.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.SkelligFileIcons
import org.skellig.plugin.language.feature.psi.PlainGherkinKeywordProvider
import javax.swing.Icon

class SkelligColorsPage : ColorSettingsPage {
    companion object {

        private val ATTRS: Array<AttributesDescriptor> = arrayOf<AttributesDescriptor>(
            AttributesDescriptor("text", SkelligHighlighter.TEXT),
            AttributesDescriptor("comment", SkelligHighlighter.COMMENT),
            AttributesDescriptor("keyword", SkelligHighlighter.KEYWORD),
            AttributesDescriptor("tag", SkelligHighlighter.TAG),
            AttributesDescriptor("pystring", SkelligHighlighter.PYSTRING),
            AttributesDescriptor("table.header.cell", SkelligHighlighter.TABLE_HEADER_CELL),
            AttributesDescriptor("table.cell", SkelligHighlighter.TABLE_CELL),
            AttributesDescriptor("table.pipe", SkelligHighlighter.PIPE),
            AttributesDescriptor("outline.param.substitution", SkelligHighlighter.OUTLINE_PARAMETER_SUBSTITUTION),
            AttributesDescriptor("regexp.param", SkelligHighlighter.REGEXP_PARAMETER)
        )

        // Empty still
        private val ADDITIONAL_HIGHLIGHT_DESCRIPTORS: MutableMap<String, TextAttributesKey> = HashMap<String, TextAttributesKey>()

        init {
            ADDITIONAL_HIGHLIGHT_DESCRIPTORS["th"] = SkelligHighlighter.TABLE_HEADER_CELL
            ADDITIONAL_HIGHLIGHT_DESCRIPTORS["outline_param"] = SkelligHighlighter.OUTLINE_PARAMETER_SUBSTITUTION
            ADDITIONAL_HIGHLIGHT_DESCRIPTORS["regexp_param"] = SkelligHighlighter.REGEXP_PARAMETER
        }
    }

    @Nullable
    override fun getIcon(): Icon? {
        return SkelligFileIcons.FEATURE_FILE
    }

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return SkelligSyntaxHighlighter(PlainGherkinKeywordProvider())
    }

    override fun getDemoText(): String =
        "# language: en\n" +
                "Feature: Cucumber Colors Settings Page\n" +
                "  In order to customize Gherkin language (*.feature files) highlighting\n" +
                "  Our users can use this settings preview pane\n" +
                "\n" +
                "  @wip\n" +
                "  Scenario Outline: Different Gherkin language structures\n" +
                "    Given Some feature file with content\n" +
                "    \"\"\"\n" +
                "    Feature: Some feature\n" +
                "      Scenario: Some scenario\n" +
                "    \"\"\"\n" +
                "    And I want to add new cucumber step\n" +
                "    And Also a step with \"<regexp_param>regexp</regexp_param>\" parameter\n" +
                "    When I open <<outline_param>ruby_ide</outline_param>>\n" +
                "    Then Steps autocompletion feature will help me with all these tasks\n" +
                "\n" +
                "  Examples:\n" +
                "    | <th>ruby_ide</th> |\n" +
                "    | RubyMine |";

    @Nullable
    override fun getAdditionalHighlightingTagToDescriptorMap(): @Nullable MutableMap<String, TextAttributesKey>? {
        return ADDITIONAL_HIGHLIGHT_DESCRIPTORS
    }

    @NotNull
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
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