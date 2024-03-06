package com.alexeymerov.radiostations.core.common

data class ThemeState(
    val darkLightMode: DarkLightMode = DarkLightMode.SYSTEM,
    val useDynamicColor: UseDynamicColor = UseDynamicColor(true),
    val colorTheme: ColorTheme = ColorTheme.DEFAULT_BLUE
)

enum class DarkLightMode(val value: Int) {
    SYSTEM(0), LIGHT(1), DARK(2), NIGHT(3)
}

enum class ColorTheme(val value: Int) {
    DEFAULT_BLUE(0), GREEN(1), ORANGE(2)
}

@JvmInline
value class UseDynamicColor(val value: Boolean)