// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.cucumber.psi.GherkinElementVisitor
import org.jetbrains.plugins.cucumber.psi.GherkinRule
import org.jetbrains.plugins.cucumber.psi.GherkinStepsHolder
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes

class GherkinRuleImpl(node: ASTNode) : GherkinPsiElementBase(node), GherkinRule {
    override fun toString(): String {
        return "GherkinRule:$ruleName"
    }

    override val ruleName: String
        get() {
            val node = node
            val firstText: ASTNode? = node.findChildByType(GherkinTokenTypes.Companion.TEXT)
            return firstText?.text ?: elementText
        }
    override val scenarios: Array<GherkinStepsHolder?>
        get() {
            val children = PsiTreeUtil.getChildrenOfType(this, GherkinStepsHolder::class.java)
            return children ?: GherkinStepsHolder.Companion.EMPTY_ARRAY
        }
    override fun getPresentableText(): String = "Rule: $ruleName"

    override fun acceptGherkin(gherkinElementVisitor: GherkinElementVisitor) {
        gherkinElementVisitor.visitRule(this)
    }
}