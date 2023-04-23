package org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import org.kepocnhh.xfiles.App

@Composable
internal fun ButtonsRow(
    modifier: Modifier,
    names: Set<String>,
    onClick: (Int) -> Unit,
) {
    Row(modifier) {
        names.forEachIndexed { index, name ->
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        onClick(index)
                    }
                    .wrapContentHeight(),
                text = name,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.text,
                ),
            )
        }
    }
}
