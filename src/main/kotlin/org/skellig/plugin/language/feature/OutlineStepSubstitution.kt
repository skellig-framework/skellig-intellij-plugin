package org.skellig.plugin.language.feature

import com.intellij.openapi.util.Pair

class OutlineStepSubstitution @JvmOverloads constructor(
    val substitution: String,
    private val offsets: List<Pair<Int, Int>>? = null
) {
    fun getOffsetInOutlineStep(offsetInSubstitutedStep: Int): Int {
        if (offsets == null) {
            return offsetInSubstitutedStep
        }
        var i = 0
        var shift = 0
        while (i < offsets.size && offsets[i].first < offsetInSubstitutedStep) {
            shift += offsets[i].second
            i++
        }
        return offsetInSubstitutedStep + shift
    }
}