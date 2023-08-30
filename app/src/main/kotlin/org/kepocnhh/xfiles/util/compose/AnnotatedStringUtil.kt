package org.kepocnhh.xfiles.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import org.kepocnhh.xfiles.module.app.Strings
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString

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
    textStyle: TextStyle,
    text: String,
) {
    val index = pushStringAnnotation(tag, annotation)
    try {
        append(
            color = textStyle.color,
            fontSize = textStyle.fontSize,
            text = text,
        )
    } finally {
        pop(index)
    }
}

//@Composable
//internal fun ClickableText(
//    modifier: Modifier = Modifier,
//    annotated: Strings.Annotated,
//    style: TextStyle = TextStyle.Default,
//    styles: @Composable (String?) -> TextStyle,
//    onClick: (String?) -> Unit
//) {
//    val text = buildAnnotatedString {
//        annotated.texts.forEachIndexed { index, text ->
//            val tag = annotated.tags[index]
//            val textStyle = styles(tag)
//            when (tag) {
//                null -> {
//                    append(
//                        color = textStyle.color,
//                        fontSize = textStyle.fontSize,
//                        text = text,
//                    )
//                }
//                else -> {
//                    append(
//                        tag = tag,
//                        annotation = "", // todo
//                        color = textStyle.color,
//                        fontSize = textStyle.fontSize,
//                        text = text,
//                    )
//                }
//            }
//        }
//    }
//    ClickableText(
//        modifier = modifier,
//        style = style,
//        text = text,
//        onClick = { offset ->
//            onClick(text.getStringAnnotations(offset, offset).singleOrNull()?.tag)
//        }
//    )
//}

@Composable
internal fun ClickableText(
    modifier: Modifier = Modifier,
    annotated: Strings.AnnotatedText,
    style: TextStyle = TextStyle.Default,
    styles: @Composable (String?) -> TextStyle,
    onClick: (String?) -> Unit
) {
    val text = buildAnnotatedString {
        val tags = annotated.tags.keys.sortedBy { it.first }
        val normal = styles(null)
        if (tags.isEmpty()) {
            append(
                color = normal.color,
                fontSize = normal.fontSize,
                text = annotated.text,
            )
        } else {
            tags.forEachIndexed { index, range ->
                val tag = annotated.tags[range]!!
                println("$index] $range tag: $tag")
                if (index == 0) {
                    val text = annotated.text.substring(0, range.first)
                    println("$index] first index text: $text")
                    append(
                        color = normal.color,
                        fontSize = normal.fontSize,
                        text = text,
                    )
                } else {
                    val text = annotated.text.substring(tags[index - 1].last + 1, range.first)
                    println("$index] text: $text")
                    append(
                        color = normal.color,
                        fontSize = normal.fontSize,
                        text = text,
                    )
                }
                append(
                    tag = annotated.tags[range]!!,
                    annotation = "", // todo
                    textStyle = styles(tag),
                    text = annotated.text.substring(range),
                )
                if (index == tags.lastIndex) {
                    val text = annotated.text.substring(range.last + 1)
                    println("$index] last index text: $text")
                    append(
                        color = normal.color,
                        fontSize = normal.fontSize,
                        text = text,
                    )
                }
            }
        }
    }
    ClickableText(
        modifier = modifier,
        style = style,
        text = text,
        onClick = { offset ->
            onClick(text.getStringAnnotations(offset, offset).singleOrNull()?.tag)
        }
    )
}
