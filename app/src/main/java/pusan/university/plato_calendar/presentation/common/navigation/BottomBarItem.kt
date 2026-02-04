package pusan.university.plato_calendar.presentation.common.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.CafeteriaScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.CalendarScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.SettingScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.ToDoScreen

enum class BottomBarItem(
    val route: PlatoCalendarScreen,
    @DrawableRes val icon: Int,
    @StringRes val titleRes: Int,
) {
    CALENDAR(
        route = CalendarScreen,
        icon = R.drawable.ic_calendar,
        titleRes = R.string.calendar,
    ),
    TODO(
        route = ToDoScreen,
        icon = R.drawable.ic_todo,
        titleRes = R.string.to_do,
    ),
    CAFETERIA(
        route = CafeteriaScreen,
        icon = R.drawable.ic_cafeteria,
        titleRes = R.string.cafeteria,
    ),
    SETTING(
        route = SettingScreen,
        icon = R.drawable.ic_setting,
        titleRes = R.string.setting,
    ),
}
