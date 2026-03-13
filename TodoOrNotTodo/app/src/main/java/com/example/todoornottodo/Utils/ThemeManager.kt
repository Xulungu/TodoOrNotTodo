package com.example.todoornottodo.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

object ThemeManager {

    fun randomLightColorScheme(): ColorScheme {
        return lightColorScheme(
            primary = randomColor(),
            onPrimary = Color.White,
            secondary = randomColor(),
            onSecondary = Color.White,
            background = randomColor(),
            onBackground = Color.Black,
            surface = randomColor(),
            onSurface = Color.Black
        )
    }

    fun randomDarkColorScheme(): ColorScheme {
        return darkColorScheme(
            primary = randomColor(),
            onPrimary = Color.Black,
            secondary = randomColor(),
            onSecondary = Color.Black,
            background = randomColor(),
            onBackground = Color.White,
            surface = randomColor(),
            onSurface = Color.White
        )
    }

    private fun randomColor(): Color {
        val r = Random.nextInt(50, 256)
        val g = Random.nextInt(50, 256)
        val b = Random.nextInt(50, 256)
        return Color(r, g, b)
    }
}