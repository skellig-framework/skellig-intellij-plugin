// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor

class GherkinTableHeaderRowImpl(node: ASTNode) : GherkinTableRowImpl(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitTableHeaderRow(this)
    }

    override fun toString(): String {
        return "GherkinTableHeaderRow"
    }
}