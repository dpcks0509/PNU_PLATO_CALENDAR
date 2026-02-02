package pusan.university.plato_calendar.presentation.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.component.PullToRefreshContainer
import pusan.university.plato_calendar.presentation.common.component.TopBar
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheet
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.todo.component.ExpandableToDoSection
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.DeleteCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.EditCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.HideScheduleBottomSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.ShowScheduleBottomSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect
import pusan.university.plato_calendar.presentation.todo.intent.ToDoState
import pusan.university.plato_calendar.presentation.todo.model.ToDoSection
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                ToDoSideEffect.HideScheduleBottomSheet -> coroutineScope.launch { sheetState.hide() }
            }
        }
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Hidden) {
            viewModel.setEvent(HideScheduleBottomSheet)
        }
    }

    ToDoContent(
        state = state,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )

    if (state.isScheduleBottomSheetVisible) {
        ScheduleBottomSheet(
            content = state.scheduleBottomSheetContent,
            selectedDate = state.today.toLocalDate(),
            sheetState = sheetState,
            makeSchedule = { },
            editSchedule = { schedule -> viewModel.setEvent(EditCustomSchedule(schedule)) },
            deleteSchedule = { id -> viewModel.setEvent(DeleteCustomSchedule(id)) },
            toggleScheduleCompletion = { id, completed ->
                viewModel.setEvent(TogglePersonalScheduleCompletion(id, completed))
            },
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoContent(
    state: ToDoState,
    onEvent: (ToDoEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val within7Days = state.within7Days
    val completedSchedules = state.completedSchedules
    val courseSchedules = state.courseSchedules
    val customSchedules = state.customSchedules
    val academicSchedules = state.academicSchedules
    var expandedToDoSections by rememberSaveable { mutableStateOf<ToDoSection?>(ToDoSection.WITHIN_7_DAYS) }

    PullToRefreshContainer(
        modifier = modifier,
        onRefresh = { onEvent(ToDoEvent.Refresh) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
        ) {
            TopBar(title = "할일")

            ToDoSection.entries.forEach { section ->
                val schedules =
                    when (section) {
                        ToDoSection.WITHIN_7_DAYS -> within7Days
                        ToDoSection.COMPLETED -> completedSchedules
                        ToDoSection.COURSE -> courseSchedules
                        ToDoSection.CUSTOM -> customSchedules
                        ToDoSection.ACADEMIC -> academicSchedules
                    }

                ExpandableToDoSection(
                    toDoSection = section,
                    items = schedules,
                    today = state.today,
                    isExpanded = expandedToDoSections == section,
                    onSectionClick = { clickedSection ->
                        expandedToDoSections =
                            if (expandedToDoSections == clickedSection) {
                                null
                            } else {
                                clickedSection
                            }
                    },
                    toggleCompletion = { id, completed ->
                        onEvent(TogglePersonalScheduleCompletion(id, completed))
                    },
                    onScheduleClick = { schedule -> onEvent(ShowScheduleBottomSheet(schedule)) },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    PlatoCalendarTheme {
        ToDoContent(
            state =
                ToDoState(
                    today = LocalDateTime.now(),
                    schedules =
                        listOf(
                            AcademicScheduleUiModel(
                                title = "신정",
                                startAt = LocalDate.of(2024, 1, 11),
                                endAt = LocalDate.now().plusDays(2),
                            ),
                            CustomScheduleUiModel(
                                id = 1L,
                                title = "새해 계획 세우기",
                                description = "",
                                startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                                endAt = LocalDateTime.now().plusDays(2).plusHours(3),
                                isCompleted = false,
                            ),
                            CourseScheduleUiModel(
                                id = 7592,
                                title = "과제1",
                                description = "",
                                startAt = LocalDateTime.now(),
                                endAt = LocalDateTime.now().plusDays(3).plusHours(7),
                                isCompleted = false,
                                courseName = "운영체제",
                            ),
                            CustomScheduleUiModel(
                                id = 2L,
                                title = "완료된 과제",
                                description = "",
                                startAt = LocalDateTime.now().minusDays(3),
                                endAt = LocalDateTime.now().minusDays(1),
                                isCompleted = true,
                            ),
                            CourseScheduleUiModel(
                                id = 7593,
                                title = "완료된 과제",
                                description = "",
                                startAt = LocalDateTime.now().minusDays(5),
                                endAt = LocalDateTime.now().minusDays(2),
                                isCompleted = true,
                                courseName = "데이터베이스",
                            ),
                        ),
                ),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
