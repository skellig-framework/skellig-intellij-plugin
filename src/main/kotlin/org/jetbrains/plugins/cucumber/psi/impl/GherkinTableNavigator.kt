// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.cucumber.psi.impl

import org.jetbrains.plugins.cucumber.psi.GherkinTableRow

/**
 * @author Roman.Chernyatchik
 * @date Sep 10, 2009
 */
object GherkinTableNavigator {
    fun getTableByRow(row: GherkinTableRow): GherkinTableImpl? {
        val element = row.parent
        return if (element is GherkinTableImpl) element else null
    }
}