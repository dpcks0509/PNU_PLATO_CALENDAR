package pusan.university.plato_calendar.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.calendar.component.Calendar
import pusan.university.plato_calendar.presentation.calendar.component.CalendarTopBar
import pusan.university.plato_calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pusan.university.plato_calendar.presentation.calendar.component.SelectedDateScheduleInfo
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.HideLoginDialog
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.HideScheduleBottomSheet
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.Refresh
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheetById
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarSideEffect
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.common.component.LoginDialog
import pusan.university.plato_calendar.presentation.common.component.PullToRefreshContainer
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheet
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.common.eventbus.NotificationEvent
import pusan.university.plato_calendar.presentation.common.eventbus.NotificationEventBus
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { if (state.today.dayOfMonth != 1) MAX_MONTH_SIZE else MAX_MONTH_SIZE - 1 },
        )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                CalendarSideEffect.HideScheduleBottomSheet -> {
                    coroutineScope.launch { sheetState.hide() }
                }

                is CalendarSideEffect.ScrollToPage -> {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            sideEffect.page,
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        NotificationEventBus.events.collect { event ->
            when (event) {
                is NotificationEvent.OpenSchedule -> {
                    viewModel.setEvent(ShowScheduleBottomSheetById(event.scheduleId))
                }

                is NotificationEvent.OpenNewSchedule -> {
                    viewModel.setEvent(ShowScheduleBottomSheet())
                }
            }
        }
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Hidden) {
            viewModel.setEvent(HideScheduleBottomSheet)
        }
    }

    CalendarContent(
        state = state,
        pagerState = pagerState,
        getMonthSchedule = viewModel::getMonthSchedule,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )

    if (state.isScheduleBottomSheetVisible) {
        ScheduleBottomSheet(
            content = state.scheduleBottomSheetContent,
            selectedDate = state.selectedDate,
            sheetState = sheetState,
            makeSchedule = { schedule -> viewModel.setEvent(MakeCustomSchedule(schedule)) },
            editSchedule = { schedule -> viewModel.setEvent(EditCustomSchedule(schedule)) },
            deleteSchedule = { id -> viewModel.setEvent(DeleteCustomSchedule(id)) },
            toggleScheduleCompletion = { id, isCompleted ->
                viewModel.setEvent(
                    TogglePersonalScheduleCompletion(
                        id,
                        isCompleted,
                    ),
                )
            },
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (state.isLoginDialogVisible) {
        LoginDialog(
            onDismissRequest = { viewModel.setEvent(HideLoginDialog) },
            onLoginRequest = { loginCredentials -> viewModel.tryLogin(loginCredentials) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    state: CalendarState,
    pagerState: PagerState,
    getMonthSchedule: (YearMonth) -> List<List<DaySchedule?>>,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    PullToRefreshContainer(
        modifier = modifier,
        onRefresh = { onEvent(Refresh) },
    ) {
        Column(
            Modifier.verticalScroll(scrollState),
        ) {
            CalendarTopBar(
                selectedDate = state.selectedDate,
                currentYearMonth = state.currentYearMonth,
                todayDate = state.today,
                moveToToday = {
                    onEvent(MoveToToday)
                },
                onMakeScheduleClick = { onEvent(ShowScheduleBottomSheet()) },
                modifier =
                    Modifier
                        .background(PrimaryColor)
                        .statusBarsPadding()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(50.dp),
            )

            Calendar(
                pagerState = pagerState,
                baseToday = state.baseToday,
                getMonthSchedule = getMonthSchedule,
                onDateClick = { date -> onEvent(UpdateSelectedDate(date)) },
                onMonthSwipe = { yearMonth -> onEvent(UpdateCurrentYearMonth(yearMonth)) },
                modifier = Modifier.fillMaxWidth(),
            )

            SelectedDateScheduleInfo(
                selectedDate = state.selectedDate,
                schedules = state.selectedDateSchedules,
                todayDate = state.today,
                onScheduleClick = { schedule -> onEvent(ShowScheduleBottomSheet(schedule)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        val base = LocalDate.of(2024, 1, 1)
        val today = LocalDate.of(2024, 1, 11)
        val schedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "신정",
                    startAt = LocalDate.of(2024, 1, 11),
                    endAt = LocalDate.of(2024, 1, 11),
                ),
                CustomScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                    endAt = LocalDateTime.of(2024, 1, 11, 16, 0),
                    isCompleted = false,
                ),
            )

        val monthSchedule =
            List(6) { week ->
                List<DaySchedule?>(7) { day ->
                    val date = base.minusDays(1).plusDays((week * 7 + day).toLong())
                    DaySchedule(
                        date = date,
                        isToday = date.dayOfMonth == 8,
                        isSelected = date.dayOfMonth == 11,
                        isInMonth = date.monthValue == 1,
                        schedules = schedules,
                    )
                }.toMutableStateList()
            }

        CalendarContent(
            state =
                CalendarState(
                    today = today,
                    selectedDate = LocalDate.of(2024, 1, 11),
                    schedules = schedules,
                    isScheduleBottomSheetVisible = false,
                    scheduleBottomSheetContent = ScheduleBottomSheetContent.NewScheduleContent,
                ),
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            getMonthSchedule = { monthSchedule },
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
