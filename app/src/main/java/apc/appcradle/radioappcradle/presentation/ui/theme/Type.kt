package apc.appcradle.radioappcradle.presentation.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import apc.appcradle.radioappcradle.R

val JuraFontFamily = FontFamily(
    Font(R.font.jura_variable_font_wght)
)

val Typography = CustomTypography(
    h1 = TextStyle(
        fontFamily = JuraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    h2 = TextStyle(
        fontFamily = JuraFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    trackText = TextStyle(
        fontFamily = JuraFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    buttonsText = TextStyle(
        fontFamily = JuraFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    labels = TextStyle(
        fontFamily = JuraFontFamily,
        fontWeight = FontWeight.W200,
        fontSize = 14.sp
    )
)

data class CustomTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val trackText: TextStyle,
    val buttonsText: TextStyle,
    val labels: TextStyle
)

