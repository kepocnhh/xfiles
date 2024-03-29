package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecuritySettings

@Composable
private fun SettingsCipher(cipher: SecurityService?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .padding(horizontal = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.cipher,
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = cipher?.algorithm.orEmpty(),
        )
    }
}

@Composable
internal fun SettingsAES(keyLength: SecuritySettings.AESKeyLength) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl),
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = App.Theme.textStyle.copy(
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.aes,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.keyLength,
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
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
            .padding(horizontal = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle.copy(
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
private fun SettingsDSADialog(
    selected: SecuritySettings.DSAKeyLength,
    onSelect: (SecuritySettings.DSAKeyLength) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = App.Theme.colors.background,
                    shape = RoundedCornerShape(App.Theme.sizes.medium),
                )
                .padding(vertical = App.Theme.sizes.medium),
        ) {
            setOf(
                SecuritySettings.DSAKeyLength.BITS_1024_1,
                SecuritySettings.DSAKeyLength.BITS_1024_2,
                SecuritySettings.DSAKeyLength.BITS_1024_3,
            ).forEach { value ->
                SettingsDSAStrengthRow(
                    value = value,
                    selected = selected == value,
                    onClick = {
                        onSelect(value)
                        onDismiss()
                    },
                )
            }
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
            .height(App.Theme.sizes.xxxl)
            .clickable(enabled = editable) {
                dialogState.value = true
            },
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = App.Theme.textStyle.copy(
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.dsa,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.keyLength,
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = getNumber(strength).toString(),
        )
    }
    if (dialogState.value) {
        SettingsDSADialog(
            selected = strength,
            onSelect = onSelectKeyLength,
            onDismiss = {
                dialogState.value = false
            },
        )
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
            .padding(horizontal = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
            ),
            text = getPretty(value),
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle.copy(
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
private fun SettingsPBEDialog(
    selected: SecuritySettings.PBEIterations,
    onSelect: (SecuritySettings.PBEIterations) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = App.Theme.colors.background,
                    shape = RoundedCornerShape(App.Theme.sizes.medium),
                )
                .padding(vertical = App.Theme.sizes.medium),
        ) {
            setOf(
                SecuritySettings.PBEIterations.NUMBER_2_10,
                SecuritySettings.PBEIterations.NUMBER_2_16,
                SecuritySettings.PBEIterations.NUMBER_2_20,
            ).forEach { value ->
                SettingsPBEIterationsRow(
                    value = value,
                    selected = selected == value,
                    onClick = {
                        onSelect(value)
                        onDismiss()
                    },
                )
            }
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
            .height(App.Theme.sizes.xxxl)
            .clickable(enabled = editable) {
                dialogState.value = true
            },
    ) {
        BasicText(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .align(Alignment.CenterStart),
            style = App.Theme.textStyle.copy(
                fontFamily = FontFamily.Monospace,
            ),
            text = App.Theme.strings.settings.pbe,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.iterations,
        )
        BasicText(
            modifier = Modifier
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
            ),
            text = getPretty(iterations),
        )
    }
    if (dialogState.value) {
        SettingsPBEDialog(
            selected = iterations,
            onSelect = onSelectIterations,
            onDismiss = {
                dialogState.value = false
            },
        )
    }
}

@Composable
internal fun SettingsCipher(
    editable: Boolean,
    cipher: SecurityService?,
    settings: SecuritySettings?,
    onSettings: (SecuritySettings) -> Unit,
) {
    Column {
        SettingsCipher(cipher)
        if (settings != null) {
            SettingsAES(keyLength = settings.aesKeyLength)
            SettingsPBE(
                editable = editable,
                iterations = settings.pbeIterations,
                onSelectIterations = {
                    onSettings(settings.copy(pbeIterations = it))
                },
            )
            SettingsDSA(
                editable = editable,
                strength = settings.dsaKeyLength,
                onSelectKeyLength = {
                    onSettings(settings.copy(dsaKeyLength = it))
                },
            )
            SettingsHasBiometric(
                hasBiometric = settings.hasBiometric,
                editable = editable,
                onClick = {
                    onSettings(settings.copy(hasBiometric = !settings.hasBiometric))
                },
            )
        }
    }
}

@Composable
private fun SettingsHasBiometric(
    hasBiometric: Boolean,
    editable: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = "SettingsScreen:cipher:biometric"
            }
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .clickable(enabled = editable, onClick = onClick)
            .padding(horizontal = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.hasBiometric,
        )
        BasicText(
            modifier = Modifier
                .semantics {
                    contentDescription = "SettingsScreen:cipher:biometric:value"
                }
                .align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = if (hasBiometric) App.Theme.strings.yes else App.Theme.strings.no,
        )
    }
}
