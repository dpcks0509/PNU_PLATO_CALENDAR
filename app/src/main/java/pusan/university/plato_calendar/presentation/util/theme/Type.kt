package pusan.university.plato_calendar.presentation.util.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import pusan.university.plato_calendar.R

private val pretendardFontFamily =
    FontFamily(
        Font(R.font.pretendard_regular, weight = FontWeight.Normal),
        Font(R.font.pretendard_medium, weight = FontWeight.Medium),
        Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
        Font(R.font.pretendard_bold, weight = FontWeight.Bold),
    )

val Typography =
    with(receiver = Typography()) {
        Typography(
            displayLarge = displayLarge.copy(fontFamily = pretendardFontFamily),
            displayMedium = displayMedium.copy(fontFamily = pretendardFontFamily),
            displaySmall = displaySmall.copy(fontFamily = pretendardFontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = pretendardFontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = pretendardFontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = pretendardFontFamily),
            titleLarge = titleLarge.copy(fontFamily = pretendardFontFamily),
            titleMedium = titleMedium.copy(fontFamily = pretendardFontFamily),
            titleSmall = titleSmall.copy(fontFamily = pretendardFontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = pretendardFontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = pretendardFontFamily),
            bodySmall = bodySmall.copy(fontFamily = pretendardFontFamily),
            labelLarge = labelLarge.copy(fontFamily = pretendardFontFamily),
            labelMedium = labelMedium.copy(fontFamily = pretendardFontFamily),
            labelSmall = labelSmall.copy(fontFamily = pretendardFontFamily),
        )
    }
