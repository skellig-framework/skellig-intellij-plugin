package org.skellig.plugin.language.testdata

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.skellig.plugin.language.SkelligFileIcons
import javax.swing.Icon

class SkelligColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Keywords", SkelligTestDataSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Separator", SkelligTestDataSyntaxHighlighter.SEPARATOR),
            AttributesDescriptor("Value", SkelligTestDataSyntaxHighlighter.DATA),
            AttributesDescriptor("String", SkelligTestDataSyntaxHighlighter.STRING),
            AttributesDescriptor("Number", SkelligTestDataSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Comment", SkelligTestDataSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Brackets", SkelligTestDataSyntaxHighlighter.SKELLIG_BRACKETS),
            AttributesDescriptor("Bad Value", SkelligTestDataSyntaxHighlighter.SKELLIG_BAD_CHARACTER)
    )

    @Nullable
    override fun getIcon(): Icon? {
        return SkelligFileIcons.TEST_DATA_FILE
    }

    @NotNull
    override fun getHighlighter(): SyntaxHighlighter {
        return SkelligTestDataSyntaxHighlighter()
    }

    @NotNull
    override fun getDemoText(): String {
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
        return "Skellig Test Data"
    }
}