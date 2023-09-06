package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecuritySettings
import kotlin.math.pow

@Composable
private fun SettingsCipher(cipher: SecurityService?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .padding(start = App.Theme.sizes.small, end = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "Cipher", // todo
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = cipher?.algorithm.orEmpty(),
        )
    }
}

private fun getPretty(value: SecuritySettings.AES.Iterations): String {
    return when (value) {
        SecuritySettings.AES.Iterations.NUMBER_2_10 -> "2^10"
        SecuritySettings.AES.Iterations.NUMBER_2_16 -> "2^16"
        SecuritySettings.AES.Iterations.NUMBER_2_20 -> "2^20"
    }
}

private fun getNumber(value: SecuritySettings.AES.Iterations): Int {
    return when (value) {
        SecuritySettings.AES.Iterations.NUMBER_2_10 -> 2.0.pow(10).toInt()
        SecuritySettings.AES.Iterations.NUMBER_2_16 -> 2.0.pow(16).toInt()
        SecuritySettings.AES.Iterations.NUMBER_2_20 -> 2.0.pow(20).toInt()
    }
}

private fun getNumber(value: SecuritySettings.DES.Strength): Int {
    return when (value) {
        SecuritySettings.DES.Strength.NUMBER_1024_1 -> 1024 * 1
        SecuritySettings.DES.Strength.NUMBER_1024_2 -> 1024 * 2
        SecuritySettings.DES.Strength.NUMBER_1024_3 -> 1024 * 3
    }
}

@Composable
internal fun SettingsAESRow(
    value: SecuritySettings.AES.Iterations,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(App.Theme.colors.background)
            .clickable(onClick = onClick),
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getPretty(value),
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getNumber(value).toString(),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .padding(end = App.Theme.sizes.small)
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "settings:aes:row:check",
                colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
            )
        }
    }
}

@Composable
internal fun SettingsDESRow(
    value: SecuritySettings.DES.Strength,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(App.Theme.colors.background)
            .clickable(onClick = onClick),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getNumber(value).toString(),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .padding(end = App.Theme.sizes.small)
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "settings:des:row:check",
                colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
            )
        }
    }
}

@Composable
internal fun SettingsAES(
    settings: SecuritySettings.AES,
    onSelectIterations: (SecuritySettings.AES.Iterations) -> Unit,
) {
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .clickable {
                dialogState.value = true
            }
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "AES", // todo
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "Iterations", // todo
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getPretty(settings.iterations),
        )
    }
    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                dialogState.value = false
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = App.Theme.colors.background,
                        shape = RoundedCornerShape(App.Theme.sizes.medium),
                    )
                    .padding(
                        top = App.Theme.sizes.medium,
                        bottom = App.Theme.sizes.medium,
                    ),
            ) {
                setOf(
                    SecuritySettings.AES.Iterations.NUMBER_2_10,
                    SecuritySettings.AES.Iterations.NUMBER_2_16,
                    SecuritySettings.AES.Iterations.NUMBER_2_20,
                ).forEach { value ->
                    SettingsAESRow(
                        value = value,
                        selected = settings.iterations == value,
                        onClick = {
                            onSelectIterations(value)
                            dialogState.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingsDES(
    settings: SecuritySettings.DES,
    onSelectStrength: (SecuritySettings.DES.Strength) -> Unit,
) {
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .clickable {
                dialogState.value = true
            },
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "DES", // todo
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "Strength", // todo
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getNumber(settings.strength).toString(),
        )
    }
    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                dialogState.value = false
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = App.Theme.colors.background,
                        shape = RoundedCornerShape(App.Theme.sizes.medium),
                    )
                    .padding(
                        top = App.Theme.sizes.medium,
                        bottom = App.Theme.sizes.medium,
                    ),
            ) {
                setOf(
                    SecuritySettings.DES.Strength.NUMBER_1024_1,
                    SecuritySettings.DES.Strength.NUMBER_1024_2,
                    SecuritySettings.DES.Strength.NUMBER_1024_3,
                ).forEach { value ->
                    SettingsDESRow(
                        value = value,
                        selected = settings.strength == value,
                        onClick = {
                            onSelectStrength(value)
                            dialogState.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingsCipher() {
    val viewModel = App.viewModel<SettingsViewModel>()
    val cipher by viewModel.cipher.collectAsState(null)
    if (cipher == null) {
        viewModel.requestCipher()
    }
    val settings = viewModel.settings.collectAsState(null).value
    if (settings == null) {
        viewModel.requestSettings()
    }
    // SecretKeyFactory
    // KeyPairGenerator
    // Signature
    Column {
        SettingsCipher(cipher)
        if (settings != null) {
//            Spacer(modifier = Modifier.height(App.Theme.sizes.small))
            SettingsAES(
                settings = settings.aes,
                onSelectIterations = {
                    viewModel.setSettings(settings.copy(aes = settings.aes.copy(iterations = it)))
                },
            )
//            Spacer(modifier = Modifier.height(App.Theme.sizes.small))
            SettingsDES(
                settings = settings.des,
                onSelectStrength = {
                    viewModel.setSettings(settings.copy(des = settings.des.copy(strength = it)))
                },
            )
        }
    }
}
