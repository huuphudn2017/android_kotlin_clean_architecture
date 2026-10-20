package com.example.base.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand palette. Change these to rebrand the app; components only read MaterialTheme.colorScheme.

private val Blue10 = Color(0xFF001A41)
private val Blue20 = Color(0xFF002E69)
private val Blue30 = Color(0xFF004494)
private val Blue40 = Color(0xFF2E5BBA)
private val Blue80 = Color(0xFFAFC6FF)
private val Blue90 = Color(0xFFD9E2FF)

private val Teal20 = Color(0xFF003731)
private val Teal30 = Color(0xFF005048)
private val Teal40 = Color(0xFF006A60)
private val Teal80 = Color(0xFF53DBC9)
private val Teal90 = Color(0xFF74F8E5)

private val Red20 = Color(0xFF690005)
private val Red30 = Color(0xFF93000A)
private val Red40 = Color(0xFFBA1A1A)
private val Red80 = Color(0xFFFFB4AB)
private val Red90 = Color(0xFFFFDAD6)

private val Neutral6 = Color(0xFF111318)
private val Neutral10 = Color(0xFF1A1B21)
private val Neutral12 = Color(0xFF1E2026)
private val Neutral17 = Color(0xFF282A2F)
private val Neutral90 = Color(0xFFE2E2E9)
private val Neutral94 = Color(0xFFEDEDF4)
private val Neutral96 = Color(0xFFF3F3FA)
private val Neutral98 = Color(0xFFF8F9FF)

private val NeutralVariant30 = Color(0xFF44474F)
private val NeutralVariant50 = Color(0xFF757780)
private val NeutralVariant60 = Color(0xFF8F9099)
private val NeutralVariant80 = Color(0xFFC5C6D0)
private val NeutralVariant90 = Color(0xFFE1E2EC)

val LightColors = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal20,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red20,
    background = Neutral98,
    onBackground = Neutral10,
    surface = Neutral98,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceContainer = Neutral94,
    surfaceContainerLow = Neutral96,
    surfaceContainerHigh = Neutral90,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
)

val DarkColors = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral6,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceContainer = Neutral12,
    surfaceContainerLow = Neutral10,
    surfaceContainerHigh = Neutral17,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
)
