package com.example.musicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Dark = darkColorScheme(primary = Color(0xFFBB86FC))
private val Light = lightColorScheme(primary = Color(0xFF6200EE))

@Composable
fun OfflineMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val c = LocalContext.current
    val cs = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(c) else dynamicLightColorScheme(c)
        darkTheme -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = cs, content = content)
}
