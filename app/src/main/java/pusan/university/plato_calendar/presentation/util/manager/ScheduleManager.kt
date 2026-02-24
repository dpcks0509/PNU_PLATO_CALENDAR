package pusan.university.plato_calendar.presentation.util.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pusan.university.plato_calendar.presentation.calendar.component.MAX_DAY_SIZE
import pusan.university.plato_calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleManager
    @Inject
    constructor() {
        private val monthlyDates = mutableMapOf<YearMonth, List<List<LocalDate?>>>()
        private val monthlySchedules = mutableMapOf<YearMonth, List<MutableList<DaySchedule?>>>()
        private val _today = MutableStateFlow(LocalDateTime.now())
        val today: StateFlow<LocalDateTime> = _today.asStateFlow()
        val baseToday: LocalDate = today.value.toLocalDate()
        private val selectedDate = MutableStateFlow(today.value.toLocalDate())

        private val _schedules = MutableStateFlow<List<ScheduleUiModel>>(emptyList())
        val schedules: StateFlow<List<ScheduleUiModel>> = _schedules.asStateFlow()

        fun updateToday() {
            val previousTodayDate = _today.value.toLocalDate()
            _today.value = LocalDateTime.now()
            val newTodayDate = _today.value.toLocalDate()

            if (previousTodayDate != newTodayDate) {
                unmarkAsToday(previousTodayDate)
                markAsToday(newTodayDate)
            }
        }

        fun updateSchedules(schedules: List<ScheduleUiModel>) {
            _schedules.value = schedules
            refreshMonthlySchedules()
        }

        fun updateSelectedDate(date: LocalDate) {
            val previousDate = selectedDate.value
            deselectDate(previousDate)
            selectDate(date)
        }

        fun getMonthSchedule(yearMonth: YearMonth): List<List<DaySchedule?>> =
            monthlySchedules.getOrPut(yearMonth) {
                generateMonthSchedule(yearMonth)
            }

        private fun refreshMonthlySchedules() {
            val groupedByDate: Map<LocalDate, List<ScheduleUiModel>> =
                _schedules.value.groupBy { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> schedule.endAt
                        is PersonalScheduleUiModel -> schedule.endAt.toLocalDate()
                    }
                }

            monthlySchedules.values.forEach { monthSchedule ->
                monthSchedule.forEach { weekSchedule ->
                    weekSchedule.forEachIndexed { index, daySchedule ->
                        val newSchedules = groupedByDate[daySchedule?.date].orEmpty()
                        if (daySchedule?.schedules != newSchedules) {
                            weekSchedule[index] = daySchedule?.copy(schedules = newSchedules)
                        }
                    }
                }
            }
        }

        private fun deselectDate(date: LocalDate) {
            monthlySchedules.values.flatten().forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isSelected = false)
                }
            }
        }

        private fun selectDate(date: LocalDate) {
            monthlySchedules.values.flatten().forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isSelected = true)
                }
            }
            selectedDate.value = date
        }

        private fun unmarkAsToday(date: LocalDate) {
            monthlySchedules.values.flatten().forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isToday = false)
                }
            }
        }

        private fun markAsToday(date: LocalDate) {
            monthlySchedules.values.flatten().forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isToday = true)
                }
            }
        }

        private fun getMonthDate(yearMonth: YearMonth): List<List<LocalDate?>> =
            monthlyDates.getOrPut(yearMonth) {
                generateMonthDate(yearMonth)
            }

        private fun generateMonthDate(yearMonth: YearMonth): List<List<LocalDate?>> {
            val baseDate = LocalDate.of(yearMonth.year, yearMonth.month, 1)
            val dayOfWeekValue = if (baseDate.dayOfWeek.value == 7) 0 else baseDate.dayOfWeek.value
            val firstDateOfMonth = baseDate.minusDays(dayOfWeekValue.toLong())

            val rangeEnd =
                baseToday
                    .plusYears(1)
                    .minusDays(1)

            val firstMonth = YearMonth(year = baseToday.year, month = baseToday.monthValue)
            val lastMonth = firstMonth.plusMonths(12)

            val firstMonthStart: LocalDate? =
                if (yearMonth == firstMonth) {
                    val monthFirstDay =
                        LocalDate.of(baseToday.year, baseToday.monthValue, 1)
                    val fiveDaysBefore = baseToday.minusDays(5)
                    if (fiveDaysBefore.isBefore(monthFirstDay)) fiveDaysBefore else monthFirstDay
                } else {
                    null
                }
            val lastMonthEnd: LocalDate? = if (yearMonth == lastMonth) rangeEnd else null

            return List(MAX_WEEK_SIZE) { weekOffset ->
                List(MAX_DAY_SIZE) { dayOffset ->
                    val date =
                        firstDateOfMonth.plusDays((weekOffset * MAX_DAY_SIZE + dayOffset).toLong())
                    val beforeStart = firstMonthStart?.let { date.isBefore(it) } ?: false
                    val afterEnd = lastMonthEnd?.let { date.isAfter(it) } ?: false
                    if (beforeStart || afterEnd) null else date
                }
            }
        }

        private fun generateMonthSchedule(yearMonth: YearMonth): List<SnapshotStateList<DaySchedule?>> =
            getMonthDate(yearMonth).map { week ->
                week
                    .map { date -> if (date != null) createDay(date, yearMonth) else null }
                    .toMutableStateList()
            }

        private fun createDay(
            date: LocalDate,
            yearMonth: YearMonth,
        ): DaySchedule {
            val isToday = date == today.value.toLocalDate()
            val isSelected = date == selectedDate.value
            val isInMonth =
                date.year == yearMonth.year && date.monthValue == yearMonth.month
            val daySchedules =
                _schedules.value.filter { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> date == schedule.endAt
                        is PersonalScheduleUiModel -> date == schedule.endAt.toLocalDate()
                    }
                }

            return DaySchedule(
                date = date,
                isToday = isToday,
                isSelected = isSelected,
                isInMonth = isInMonth,
                schedules = daySchedules,
            )
        }
    }
