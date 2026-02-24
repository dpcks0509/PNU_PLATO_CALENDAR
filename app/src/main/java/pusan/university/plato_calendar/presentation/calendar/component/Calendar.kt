package pusan.university.plato_calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

const val MAX_MONTH_SIZE = 13
const val MAX_WEEK_SIZE = 6
const val MAX_DAY_SIZE = 7

@Composable
fun Calendar(
    pagerState: PagerState,
    baseToday: LocalDate,
    getMonthSchedule: (YearMonth) -> List<List<DaySchedule?>>,
    onDateClick: (LocalDate) -> Unit,
    onMonthSwipe: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseYearMonth = YearMonth(baseToday.year, baseToday.monthValue)

    LaunchedEffect(pagerState.currentPage) {
        val yearMonth = baseYearMonth.plusMonths(pagerState.currentPage)
        onMonthSwipe(yearMonth)
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier,
    ) { page ->
        val yearMonth = remember(page) { baseYearMonth.plusMonths(page) }
        val monthSchedule = remember(yearMonth) { getMonthSchedule(yearMonth) }

        Column {
            DayOfWeekHeader(modifier = Modifier.padding(top = 4.dp))

            MonthItem(
                monthSchedule = monthSchedule,
                onDateClick = onDateClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    PlatoCalendarTheme {
        val today = LocalDate.of(2024, 1, 8)
        val baseToday = LocalDate.of(2024, 1, 8)
        val schedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "신정",
                    startAt = LocalDate.of(2024, 1, 1),
                    endAt = LocalDate.of(2024, 1, 1),
                ),
                CustomScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                    endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                    isCompleted = false,
                ),
            )

        Calendar(
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            baseToday = baseToday,
            getMonthSchedule = { yearMonth ->
                val firstDayOfMonth = LocalDate.of(yearMonth.year, yearMonth.month, 1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                val startDate = firstDayOfMonth.minusDays(firstDayOfWeek.toLong())

                List(MAX_WEEK_SIZE) { week ->
                    List(MAX_DAY_SIZE) { day ->
                        val currentDate = startDate.plusDays((week * MAX_DAY_SIZE + day).toLong())
                        DaySchedule(
                            date = currentDate,
                            isToday = currentDate == today,
                            isSelected = currentDate == today.plusDays(3),
                            isInMonth = currentDate.monthValue == yearMonth.month && currentDate.year == yearMonth.year,
                            schedules = if (currentDate.dayOfMonth in listOf(1, 3)) schedules else emptyList(),
                        )
                    }.toMutableStateList()
                }
            },
            onDateClick = {},
            onMonthSwipe = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
