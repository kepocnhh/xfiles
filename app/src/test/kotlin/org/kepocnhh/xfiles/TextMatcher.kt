package org.kepocnhh.xfiles

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

internal fun textMatcher(description: String, predicate: (String) -> Boolean): SemanticsMatcher {
    return SemanticsMatcher(description) { node ->
        val list = node.config.getOrNull(SemanticsProperties.Text)
        when {
            list == null -> false
            list.size != 1 -> false
            else -> predicate(list.single().text)
        }
    }
}
