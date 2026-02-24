package pusan.university.plato_calendar.presentation.common.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF3B6EC7)
val PrimaryDark = Color(0xFF3B6EC7)

val WhiteLight = Color.White
val WhiteDark = Color(0xFF121212)

val BlackLight = Color.Black
val BlackDark = Color.White

val VeryLightGrayLight = Color(0xFFF2F2F2)
val VeryLightGrayDark = Color(0xFF2A2A2A)

val LightBlueLight = Color(0xFFAAD0F5)
val LightBlueDark = Color(0xFF1E3A5F)


val LightGrayLight = Color(0xFFDDDDDD)
val LightGrayDark = Color(0xFF444444)

val MediumGrayLight = Color(0xFFCCCCCC)
val MediumGrayDark = Color(0xFF666666)

val GrayLight = Color(0x99000000)
val GrayDark = Color(0x99FFFFFF)

val RedLight = Color.Red
val RedDark = Color(0xFFFF8A80)

val LightYellowLight = Color(0xFFFFF9E6)
val LightYellowDark = Color(0xFF3A3420)

val YellowLight = Color(0xFFFFE082)
val YellowDark = Color(0xFF5A5020)

val BrownLight = Color(0xFF6D4C00)
val BrownDark = Color(0xFFFFD580)

val CalendarFlamingoLight = Color(0xFFE67C73)
val CalendarFlamingoDark = Color(0xFFF6AEA9)

val CalendarSageLight = Color(0xFF33B679)
val CalendarSageDark = Color(0xFF81C995)

val CalendarLavenderLight = Color(0xFF7986CB)
val CalendarLavenderDark = Color(0xFF9FA8DA)

val PrimaryColor: Color
    @Composable get() = if (LocalDarkTheme.current) PrimaryDark else PrimaryLight

val Black: Color
    @Composable get() = if (LocalDarkTheme.current) BlackDark else BlackLight

val White: Color
    @Composable get() = if (LocalDarkTheme.current) WhiteDark else WhiteLight

val WhiteGray: Color
    @Composable get() = if (LocalDarkTheme.current) VeryLightGrayDark else WhiteLight

val LightBlue: Color
    @Composable get() = if (LocalDarkTheme.current) LightBlueDark else LightBlueLight

val VeryLightGray: Color
    @Composable get() = if (LocalDarkTheme.current) VeryLightGrayDark else VeryLightGrayLight

val LightGray: Color
    @Composable get() = if (LocalDarkTheme.current) LightGrayDark else LightGrayLight

val MediumGray: Color
    @Composable get() = if (LocalDarkTheme.current) MediumGrayDark else MediumGrayLight

val Gray: Color
    @Composable get() = if (LocalDarkTheme.current) GrayDark else GrayLight

val Red: Color
    @Composable get() = if (LocalDarkTheme.current) RedDark else RedLight

val LightYellow: Color
    @Composable get() = if (LocalDarkTheme.current) LightYellowDark else LightYellowLight

val Yellow: Color
    @Composable get() = if (LocalDarkTheme.current) YellowDark else YellowLight

val Brown: Color
    @Composable get() = if (LocalDarkTheme.current) BrownDark else BrownLight

val CalendarFlamingo: Color
    @Composable get() = CalendarFlamingoLight
//    if (LocalDarkTheme.current) CalendarFlamingoDark else CalendarFlamingoLight

val CalendarSage: Color
    @Composable get() = CalendarSageLight
//    if (LocalDarkTheme.current) CalendarSageDark else CalendarSageLight

val CalendarLavender: Color
    @Composable get() = CalendarLavenderLight
//    if (LocalDarkTheme.current) CalendarLavenderDark else CalendarLavenderLight
