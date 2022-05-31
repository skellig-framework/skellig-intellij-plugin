package org.jetbrains.plugins.cucumber.psi

interface GherkinScenario : GherkinStepsHolder {
    val isBackground: Boolean
}