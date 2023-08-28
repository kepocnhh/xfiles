package org.kepocnhh.xfiles.util.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit

internal fun AnnotatedString.Builder.append(
    color: Color,
    fontSize: TextUnit,
    text: String,
) {
    val index = pushStyle(
        SpanStyle(
            color = color,
            fontSize = fontSize,
        ),
    )
    try {
        append(text)
    } finally {
        pop(index)
    }
}

internal fun AnnotatedString.Builder.append(
    tag: String,
    annotation: String,
    color: Color,
    fontSize: TextUnit,
    text: String,
) {
    val index = pushStringAnnotation(tag, annotation)
    try {
        append(
            color = color,
            fontSize = fontSize,
            text = text,
        )
    } finally {
        pop(index)
    }
}
