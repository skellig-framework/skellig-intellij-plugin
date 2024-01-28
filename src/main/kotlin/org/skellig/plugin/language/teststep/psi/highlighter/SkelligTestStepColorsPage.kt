package org.skellig.plugin.language.teststep.psi.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon


class SkelligTestStepColorsPage : ColorSettingsPage {

    companion object {
        private val ATTRS: Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor("TEXT", SkelligTestStepHighlighter.TEXT),
            AttributesDescriptor("COMMENT", SkelligTestStepHighlighter.COMMENT),
            AttributesDescriptor("KEYWORD", SkelligTestStepHighlighter.KEYWORD),
            AttributesDescriptor("STRING", SkelligTestStepHighlighter.STRING),
            AttributesDescriptor("BRACKETS", SkelligTestStepHighlighter.BRACKETS),
            AttributesDescriptor("REFERENCE", SkelligTestStepHighlighter.REFERENCE),
            AttributesDescriptor("FUNCTION", SkelligTestStepHighlighter.FUNCTION),
            AttributesDescriptor("OPERATION", SkelligTestStepHighlighter.OPERATION),
            AttributesDescriptor("NUMBER", SkelligTestStepHighlighter.NUMBER),
        )
    }

    @Nullable
    override fun getIcon(): Icon {
        return SkelligFileIcons.FEATURE_FILE
    }

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return SkelligTestStepSyntaxHighlighter()
    }

    override fun getDemoText(): String =
        """
           // comment
           name ("test 1") {
              id = test1
              
              values {
                 a = "string value"
                 b [
                   1,
                   2
                 ]
                 c = $\{property1}
              }
              
              validate {
                 body.jsonPath(a.b.c) = contains(text)
                 status = $\{status}
              }
           }
        """.replace("\\", "").trimIndent()

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