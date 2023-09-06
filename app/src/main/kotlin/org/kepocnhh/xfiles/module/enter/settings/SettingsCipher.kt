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
import androidx.compose.ui.text.font.FontFamily
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
            .height(SettingsScreen.LocalSizes.current.rowHeight)
            .padding(start = App.Theme.sizes.small, end = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.settings.cipher,
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = cipher?.algorithm.orEmpty(),
        )
    }
}

private fun getNumber(value: SecuritySettings.AESKeyLength): Int {
    return when (value) {
        SecuritySettings.AESKeyLength.BITS_256 -> 256
    }
}

private fun getNumber(value: SecuritySettings.DSAKeyLength): Int {
    return when (value) {
        SecuritySettings.DSAKeyLength.BITS_1024_1 -> 1024 * 1
        SecuritySettings.DSAKeyLength.BITS_1024_2 -> 1024 * 2
        SecuritySettings.DSAKeyLength.BITS_1024_3 -> 1024 * 3
    }
}

private fun getNumber(value: SecuritySettings.PBEIterations): Int {
    return when (value) {
        SecuritySettings.PBEIterations.NUMBER_2_10 -> 2.0.pow(10).toInt()
        SecuritySettings.PBEIterations.NUMBER_2_16 -> 2.0.pow(16).toInt()
        SecuritySettings.PBEIterations.NUMBER_2_20 -> 2.0.pow(20).toInt()
    }
}

private fun getPretty(value: SecuritySettings.PBEIterations): String {
    return when (value) {
        SecuritySettings.PBEIterations.NUMBER_2_10 -> "2^10"
        SecuritySettings.PBEIterations.NUMBER_2_16 -> "2^16"
        SecuritySettings.PBEIterations.NUMBER_2_20 -> "2^20"
    }
}

@Composable
internal fun SettingsAES(keyLength: SecuritySettings.AESKeyLength) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsScreen.LocalSizes.current.rowHeight),
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.aes,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.settings.keyLength,
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = getNumber(keyLength).toString(),
        )
    }
}

@Composable
internal fun SettingsDSAStrengthRow(
    value: SecuritySettings.DSAKeyLength,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(App.Theme.colors.background)
            .clickable(onClick = onClick)
            .padding(start = App.Theme.sizes.small, end = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = getNumber(value).toString(),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "settings:dsa:row:check",
                colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
            )
        }
    }
}

@Composable
internal fun SettingsDSA(
    editable: Boolean,
    strength: SecuritySettings.DSAKeyLength,
    onSelectKeyLength: (SecuritySettings.DSAKeyLength) -> Unit,
) {
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsScreen.LocalSizes.current.rowHeight)
            .clickable(enabled = editable) {
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
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.dsa,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.settings.keyLength,
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = getNumber(strength).toString(),
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
                    SecuritySettings.DSAKeyLength.BITS_1024_1,
                    SecuritySettings.DSAKeyLength.BITS_1024_2,
                    SecuritySettings.DSAKeyLength.BITS_1024_3,
                ).forEach { value ->
                    SettingsDSAStrengthRow(
                        value = value,
                        selected = strength == value,
                        onClick = {
                            onSelectKeyLength(value)
                            dialogState.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingsPBEIterationsRow(
    value: SecuritySettings.PBEIterations,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(App.Theme.colors.background)
            .clickable(onClick = onClick)
            .padding(start = App.Theme.sizes.small, end = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
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
                fontFamily = FontFamily.Monospace,
            ),
            text = getNumber(value).toString(),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "settings:pbe:row:check",
                colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
            )
        }
    }
}

@Composable
internal fun SettingsPBE(
    editable: Boolean,
    iterations: SecuritySettings.PBEIterations,
    onSelectIterations: (SecuritySettings.PBEIterations) -> Unit,
) {
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsScreen.LocalSizes.current.rowHeight)
            .clickable(enabled = editable) {
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
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.pbe,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.settings.iterations,
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
            text = getPretty(iterations),
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
                    SecuritySettings.PBEIterations.NUMBER_2_10,
                    SecuritySettings.PBEIterations.NUMBER_2_16,
                    SecuritySettings.PBEIterations.NUMBER_2_20,
                ).forEach { value ->
                    SettingsPBEIterationsRow(
                        value = value,
                        selected = iterations == value,
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
internal fun SettingsCipher(editable: Boolean) {
    val viewModel = App.viewModel<SettingsViewModel>()
    val cipher by viewModel.cipher.collectAsState(null)
    if (cipher == null) {
        viewModel.requestCipher()
    }
    val settings = viewModel.settings.collectAsState(null).value
    if (settings == null) {
        viewModel.requestSettings()
    }
    Column {
        SettingsCipher(cipher)
        if (settings != null) {
            SettingsAES(keyLength = settings.aesKeyLength)
            SettingsPBE(
                editable = editable,
                iterations = settings.pbeIterations,
                onSelectIterations = {
                    viewModel.setSettings(settings.copy(pbeIterations = it))
                },
            )
            SettingsDSA(
                editable = editable,
                strength = settings.dsaKeyLength,
                onSelectKeyLength = {
                    viewModel.setSettings(settings.copy(dsaKeyLength = it))
                },
            )
        }
    }
}
