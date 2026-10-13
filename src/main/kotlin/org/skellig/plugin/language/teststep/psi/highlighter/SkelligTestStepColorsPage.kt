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
        private val ATTRS: Array<AttributesDescriptor> =
            arrayOf(
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
    override fun getIcon(): Icon = SkelligFileIcons.FEATURE_FILE

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter = SkelligTestStepSyntaxHighlighter()

    override fun getDemoText(): String =
        """
           // comment
           <keyword>name</keyword> ("test 1") {
              id = test1
              
              <keyword>values</keyword> {
                 a = "string value"
                 b [
                   1,
                   2
                 ]
                 c = $\{property1}
              }
              
              <keyword>validate</keyword> {
                 body.jsonPath(a.b.c) = contains(text)
                 status = $\{status}
              }
           }
        """.replace("\\", "").trimIndent()

    @Nullable
    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> =
        mutableMapOf("keyword" to SkelligTestStepHighlighter.KEYWORD)

    @NotNull
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRS

    @NotNull
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    @NotNull
    override fun getDisplayName(): String = "Skellig Test Step"
}
