package org.skellig.plugin.language.feature.psi

interface GherkinScenario : GherkinStepsHolder {
    val isBackground: Boolean
}