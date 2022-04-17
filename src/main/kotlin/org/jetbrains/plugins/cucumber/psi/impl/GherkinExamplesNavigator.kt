// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

/**
 * @author Roman.Chernyatchik
 * @date Sep 11, 2009
 */
object GherkinExamplesNavigator {
    fun getExamplesByTable(table: GherkinTableImpl): GherkinExamplesBlockImpl? {
        val element = table.parent
        return if (element is GherkinExamplesBlockImpl) element else null
    }
}