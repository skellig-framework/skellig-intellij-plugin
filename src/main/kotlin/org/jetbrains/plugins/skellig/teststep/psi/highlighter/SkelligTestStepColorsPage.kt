package org.jetbrains.plugins.skellig.teststep.psi.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.cucumber.psi.PlainGherkinKeywordProvider
import org.jetbrains.plugins.skellig.teststep.psi.PlainSkelligTestStepKeywordProvider
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon


class SkelligTestStepColorsPage : ColorSettingsPage {
    companion object {
        private val ATTRS: Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor("text", SkelligTestStepHighlighter.TEXT),
            AttributesDescriptor("comment", SkelligTestStepHighlighter.COMMENT),
            AttributesDescriptor("keyword", SkelligTestStepHighlighter.KEYWORD),
            AttributesDescriptor("string", SkelligTestStepHighlighter.STRING),
            AttributesDescriptor("brackets", SkelligTestStepHighlighter.BRACKETS),
            AttributesDescriptor("property", SkelligTestStepHighlighter.PROPERTY),
            AttributesDescriptor("parameter", SkelligTestStepHighlighter.PARAMETER),
        )
    }

    @Nullable
    override fun getIcon(): Icon {
        return SkelligFileIcons.FEATURE_FILE
    }

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return SkelligTestStepSyntaxHighlighter(PlainSkelligTestStepKeywordProvider())
    }

    override fun getDemoText(): String =
        """
           // comment
           name (test 1) {
              param1 = 100
              
              variables {
                 a = 1
                 b [
                   1
                   2
                 ]
                 c = \$\{key}
              }
           }
        """.trimIndent()

    @Nullable
    override fun getAdditionalHighlightingTagToDescriptorMap(): @Nullable MutableMap<String, TextAttributesKey>? {
        return null
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
        return "Skellig Test Step"
    }

}