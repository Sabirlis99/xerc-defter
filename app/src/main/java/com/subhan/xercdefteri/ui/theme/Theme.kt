package com.subhan.xercdefteri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LedgerColorScheme = lightColorScheme(
    primary = Ink,
    background = Paper,
    surface = Card,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun XercDefteriTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LedgerColorScheme,
        typography = AppTypography,
        content = content
    )
}
