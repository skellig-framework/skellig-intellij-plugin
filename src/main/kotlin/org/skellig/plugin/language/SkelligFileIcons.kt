package org.skellig.plugin.language

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon;


class SkelligFileIcons {
    companion object {
        val FEATURE_FILE: Icon = IconLoader.getIcon("/icons/skellig.svg", SkelligFileIcons::class.java)
        val TEST_DATA_FILE: Icon = IconLoader.getIcon("/icons/sts_icon.svg", SkelligFileIcons::class.java)
    }
}