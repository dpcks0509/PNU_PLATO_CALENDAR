package pusan.university.plato_calendar.presentation.setting.model

import androidx.annotation.DrawableRes
import pusan.university.plato_calendar.R

enum class ThemeMode(
    val label: String,
    @get:DrawableRes val iconRes: Int,
) {
    LIGHT("라이트", R.drawable.ic_light),
    DARK("다크", R.drawable.ic_dark),
    SYSTEM("시스템", R.drawable.ic_system),
}
