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
import java.util.regex.Pattern

internal fun AnnotatedString.Builder.append(
    textStyle: TextStyle,
    text: String,
) {
    val index = pushStyle(
        SpanStyle(
            color = textStyle.color,
            fontSize = textStyle.fontSize,
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
            textStyle = textStyle,
            text = text,
        )
    } finally {
        pop(index)
    }
}

private object ClickableTextUtil {
    const val all = "\\w\\d\\s.!?-"
    val regexTags = "\\[[$all]+\\]\\([$all]+\\)".toRegex()
    val betweenSquareBrackets = "(?<=\\[)[\\S\\s]+(?=\\])".toRegex()
    val betweenRoundBrackets = "(?<=\\()[\\S\\s]+(?=\\))".toRegex()
}

@Composable
internal fun ClickableText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = TextStyle.Default,
    styles: Map<String, TextStyle>,
    onClick: (String?) -> Unit
) {
    val split = ClickableTextUtil.regexTags.split(text)
    val matches = ClickableTextUtil
        .regexTags
        .findAll(text)
        .map { it.value }
        .toList()
    val first = split.firstOrNull()
    val annotated: AnnotatedString = if (first == null) {
        AnnotatedString.Builder().toAnnotatedString()
    } else {
        buildAnnotatedString {
            if (first.isNotEmpty()) {
                append(first)
            }
            for (i in 1 until split.size) {
                val match = matches[i - 1]
                val tag = ClickableTextUtil
                    .betweenSquareBrackets
                    .find(match)
                    ?.value
                check(!tag.isNullOrEmpty())
                val value = ClickableTextUtil
                    .betweenRoundBrackets
                    .find(match)
                    ?.value
                check(!value.isNullOrEmpty())
                append(
                    tag = tag,
                    annotation = "", // todo
                    textStyle = styles[tag] ?: style,
                    text = value,
                )
                val it = split[i]
                if (it.isNotEmpty()) {
                    append(it)
                }
            }
        }
    }
    ClickableText(
        modifier = modifier,
        style = style,
        text = annotated,
        onClick = { offset ->
            onClick(annotated.getStringAnnotations(offset, offset).singleOrNull()?.tag)
        }
    )
}
