package pusan.university.plato_calendar.presentation.common.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.common.icon.IconType
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.CafeteriaScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.CalendarScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.SettingScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.ToDoScreen

enum class BottomBarItem(
    val route: PlatoCalendarScreen,
    val icon: IconType,
    @StringRes val titleRes: Int,
) {
    CALENDAR(
        route = CalendarScreen,
        icon = IconType.Vector(Icons.Default.DateRange),
        titleRes = R.string.calendar,
    ),
    TODO(
        route = ToDoScreen,
        icon = IconType.Resource(R.drawable.ic_todo),
        titleRes = R.string.to_do,
    ),
    CAFETERIA(
        route = CafeteriaScreen,
        icon = IconType.Resource(R.drawable.ic_cafeteria),
        titleRes = R.string.cafeteria,
    ),
    SETTING(
        route = SettingScreen,
        icon = IconType.Vector(Icons.Default.Settings),
        titleRes = R.string.setting,
    ),
}
