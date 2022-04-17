// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor

class GherkinFeatureHeaderImpl(node: ASTNode) : GherkinPsiElementBase(node) {
    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitFeatureHeader(this)
    }

    override fun toString(): String {
        return "GherkinFeatureHeader"
    }
}