package com.mindmatrix.gramasanjeevini.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val EmergencyRed = Color(0xFFD32F2F)
val AvailabilityGreen = Color(0xFF388E3C)
val PrimaryBlue = Color(0xFF1976D2)
val WarningAmber = Color(0xFFF57C00)
val Ink = Color(0xFF162029)
val SurfaceSoft = Color(0xFFF9FBF8)
val MedicalBlueSoft = Color(0xFFEAF8FA)
val CardSoft = Color(0xFFF4FAF8)

private val AppColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = AvailabilityGreen,
    onSecondary = Color.White,
    error = EmergencyRed,
    onError = Color.White,
    background = SurfaceSoft,
    onBackground = Ink,
    surface = CardSoft,
    onSurface = Ink,
    surfaceVariant = MedicalBlueSoft,
    onSurfaceVariant = Color(0xFF526568),
)

private val AppDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF08385F),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF0B3D16),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF4B0000),
    background = Color(0xFF101820),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF182330),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF253241),
    onSurfaceVariant = Color(0xFFC4D0DC),
)

private val AppTypography = Typography(
    bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    titleLarge = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    labelLarge = TextStyle(fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
)

@Composable
fun GramaSanjeeviniTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AppDarkColorScheme else AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}
