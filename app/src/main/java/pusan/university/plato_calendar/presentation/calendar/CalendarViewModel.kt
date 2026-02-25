package pusan.university.plato_calendar.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException.Companion.NETWORK_ERROR_MESSAGE
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.usecase.course.GetCourseNameUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.DeleteCustomScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.EditPersonalScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAcademicSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetPersonalSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MakeCustomScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MarkScheduleAsCompletedUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.MarkScheduleAsUncompletedUseCase
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.Login
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.Refresh
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheetById
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDateByWidget
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarSideEffect
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.util.base.BaseViewModel
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.NewScheduleContent
import pusan.university.plato_calendar.presentation.util.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.util.manager.LoadingManager
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import pusan.university.plato_calendar.presentation.util.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
    private val scheduleManager: ScheduleManager,
    private val loadingManager: LoadingManager,
    private val alarmScheduler: AlarmScheduler,
    private val getAcademicSchedulesUseCase: GetAcademicSchedulesUseCase,
    private val getPersonalSchedulesUseCase: GetPersonalSchedulesUseCase,
    private val getCourseNameUseCase: GetCourseNameUseCase,
    private val makeCustomScheduleUseCase: MakeCustomScheduleUseCase,
    private val editPersonalScheduleUseCase: EditPersonalScheduleUseCase,
    private val deleteCustomScheduleUseCase: DeleteCustomScheduleUseCase,
    private val markScheduleAsCompletedUseCase: MarkScheduleAsCompletedUseCase,
    private val markScheduleAsUncompletedUseCase: MarkScheduleAsUncompletedUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(
    initialState =
        CalendarState(
            today = scheduleManager.today.value.toLocalDate(),
            selectedDate = scheduleManager.today.value.toLocalDate(),
        ),
) {
    private var pendingOpenScheduleId: Long?
        get() = savedStateHandle[KEY_PENDING_OPEN_SCHEDULE_ID]
        set(value) {
            savedStateHandle[KEY_PENDING_OPEN_SCHEDULE_ID] = value
        }

    init {
        viewModelScope.launch {
            launch {
                loginManager.loginStatus.collect {
                    getSchedules()
                }
            }

            launch {
                scheduleManager.schedules.collect { schedules ->
                    setState { copy(schedules = schedules) }
                }
            }

            launch {
                scheduleManager.today.collect { today ->
                    setState { copy(today = today.toLocalDate()) }
                }
            }
        }
    }

    override suspend fun handleEvent(event: CalendarEvent) {
        when (event) {
            is Login -> {
                loginManager.login(credentials = event.credentials)
            }

            MoveToToday -> {
                val today = scheduleManager.today.value.toLocalDate()
                val baseToday = scheduleManager.baseToday
                val todayYearMonth = YearMonth(year = today.year, month = today.monthValue)
                val baseTodayYearMonth =
                    YearMonth(year = baseToday.year, month = baseToday.monthValue)

                val monthsDiff =
                    (todayYearMonth.year - baseTodayYearMonth.year) * 12 +
                            (todayYearMonth.month - baseTodayYearMonth.month)

                scheduleManager.updateSelectedDate(today)

                setState {
                    copy(
                        selectedDate = today,
                        currentYearMonth = todayYearMonth,
                        today = today,
                    )
                }

                setSideEffect { CalendarSideEffect.ScrollToPage(monthsDiff) }
            }

            Refresh -> {
                refresh()
            }

            is MakeCustomSchedule -> {
                makeCustomSchedule(event.schedule)
            }

            is EditCustomSchedule -> {
                editCustomSchedule(event.schedule)
            }

            is DeleteCustomSchedule -> {
                deleteCustomSchedule(event.id)
            }

            is TogglePersonalScheduleCompletion -> {
                togglePersonalScheduleCompletion(
                    event.id,
                    event.isCompleted,
                )
            }

            is UpdateSelectedDate -> {
                scheduleManager.updateSelectedDate(event.date)
                setState { copy(selectedDate = event.date) }
            }

            is UpdateSelectedDateByWidget -> {
                navigateToDate(event.date)
            }

            is UpdateCurrentYearMonth -> {
                setState { copy(currentYearMonth = event.yearMonth) }
            }

            is ShowScheduleBottomSheet -> {
                showScheduleBottomSheet(schedule = event.schedule)
            }

            is ShowScheduleBottomSheetById -> {
                showScheduleBottomSheetById(scheduleId = event.scheduleId)
            }

            is CalendarEvent.ShowDialog -> {
                setState { copy(scheduleDialogContent = event.content) }
            }

            CalendarEvent.HideDialog -> {
                setState { copy(scheduleDialogContent = null) }
            }
        }
    }

    private fun refresh() {
        scheduleManager.updateToday()
        getSchedules()
    }

    fun getMonthSchedule(yearMonth: YearMonth): List<List<DaySchedule?>> = scheduleManager.getMonthSchedule(yearMonth)

    private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> =
        when (val result = getAcademicSchedulesUseCase()) {
            is ApiResult.Success -> {
                result.data
                    .map(::AcademicScheduleUiModel)
                    .toSet()
                    .toList()
            }

            is ApiResult.Error -> {
                if (result.exception !is NoNetworkConnectivityException) {
                    ToastEventBus.sendError(result.exception.message)
                }
                emptyList()
            }
        }

    private suspend fun getPersonalSchedules(sessKey: String): List<ScheduleUiModel> =
        when (val result = getPersonalSchedulesUseCase(sessKey)) {
            is ApiResult.Success -> {
                result.data.map { domain ->
                    when (domain) {
                        is CourseSchedule -> {
                            val courseName =
                                getCourseNameUseCase(domain.courseCode)

                            CourseScheduleUiModel(
                                domain = domain,
                                courseName = courseName,
                            )
                        }

                        is CustomSchedule -> {
                            CustomScheduleUiModel(domain)
                        }
                    }
                }
            }

            is ApiResult.Error -> {
                loadingManager.updateLoading(false)
                ToastEventBus.sendError(result.exception.message)
                emptyList()
            }
        }

    private fun getSchedules() {
        viewModelScope
            .launch {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        loadingManager.updateLoading(true)

                        val (academicSchedules, personalSchedules) =
                            awaitAll(
                                async { getAcademicSchedules() },
                                async { getPersonalSchedules(loginStatus.loginSession.sessKey) },
                            )

                        val schedules = academicSchedules + personalSchedules

                        if (schedules.isNotEmpty()) scheduleManager.updateSchedules(schedules)
                        loadingManager.updateLoading(false)

                        pendingOpenScheduleId?.let { id ->
                            val targetSchedule =
                                schedules
                                    .filterIsInstance<PersonalScheduleUiModel>()
                                    .find { it.id == id }

                            if (targetSchedule != null) {
                                navigateToDate(targetSchedule.endAt.toLocalDate())
                                showScheduleBottomSheet(targetSchedule)
                                pendingOpenScheduleId = null
                            }
                        }
                    }

                    LoginStatus.Logout -> {
                        loadingManager.updateLoading(true)

                        val academicSchedules = getAcademicSchedules()

                        scheduleManager.updateSchedules(academicSchedules)
                    }

                    LoginStatus.NetworkDisconnected -> {
                        ToastEventBus.sendError(NETWORK_ERROR_MESSAGE)
                    }

                    LoginStatus.Uninitialized, LoginStatus.LoginInProgress -> {
                        Unit
                    }
                }
            }.invokeOnCompletion {
                loadingManager.updateLoading(false)
            }
    }

    private suspend fun makeCustomSchedule(newSchedule: NewSchedule) {
        when (val result = makeCustomScheduleUseCase(newSchedule)) {
            is ApiResult.Success -> {
                val customSchedule =
                    CustomScheduleUiModel(
                        id = result.data,
                        title = newSchedule.title,
                        description = newSchedule.description,
                        startAt = newSchedule.startAt,
                        endAt = newSchedule.endAt,
                        isCompleted = false,
                    )
                val updatedSchedules = state.value.schedules + customSchedule
                scheduleManager.updateSchedules(updatedSchedules)

                setState { copy(scheduleBottomSheetContent = null) }
                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 생성되었습니다.")
            }

            is ApiResult.Error -> {
                ToastEventBus.sendError(result.exception.message)
            }
        }
    }

    private suspend fun editCustomSchedule(customSchedule: CustomSchedule) {
        when (val result = editPersonalScheduleUseCase(customSchedule)) {
            is ApiResult.Success -> {
                val updatedSchedules =
                    state.value.schedules.map { schedule ->
                        if (schedule is CustomScheduleUiModel && schedule.id == customSchedule.id) {
                            schedule.copy(
                                title = customSchedule.title,
                                description = customSchedule.description,
                                startAt = customSchedule.startAt,
                                endAt = customSchedule.endAt,
                                isCompleted = customSchedule.isCompleted,
                            )
                        } else {
                            schedule
                        }
                    }
                scheduleManager.updateSchedules(updatedSchedules)

                setState { copy(scheduleBottomSheetContent = null) }
                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 수정되었습니다.")
            }

            is ApiResult.Error -> {
                ToastEventBus.sendError(result.exception.message)
            }
        }
    }

    private suspend fun deleteCustomSchedule(id: Long) {
        when (val result = deleteCustomScheduleUseCase(id)) {
            is ApiResult.Success -> {
                val updatedSchedules =
                    state.value.schedules.filter { schedule ->
                        !(schedule is PersonalScheduleUiModel && schedule.id == id)
                    }
                scheduleManager.updateSchedules(updatedSchedules)
                alarmScheduler.cancelNotification(id)

                setState { copy(scheduleBottomSheetContent = null) }
                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 삭제되었습니다.")
            }

            is ApiResult.Error -> {
                ToastEventBus.sendError(result.exception.message)
            }
        }
    }

    private suspend fun togglePersonalScheduleCompletion(
        id: Long,
        isCompleted: Boolean,
    ) {
        if (isCompleted) {
            markScheduleAsCompletedUseCase(id)
        } else {
            markScheduleAsUncompletedUseCase(id)
        }

        val updatedSchedules =
            state.value.schedules.map { schedule ->
                if (schedule is PersonalScheduleUiModel && schedule.id == id) {
                    when (schedule) {
                        is CourseScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                        is CustomScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                    }
                } else {
                    schedule
                }
            }
        scheduleManager.updateSchedules(updatedSchedules)

        setState { copy(scheduleBottomSheetContent = null) }
        setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
        ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
    }

    private fun showScheduleBottomSheet(schedule: ScheduleUiModel?) {
        val content =
            when (schedule) {
                is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                is AcademicScheduleUiModel -> AcademicScheduleContent(schedule)
                null -> NewScheduleContent
            }

        if (loginManager.loginStatus.value is LoginStatus.Login) {
            setState { copy(scheduleBottomSheetContent = content) }
            setSideEffect { CalendarSideEffect.ShowScheduleBottomSheet }
        } else {
            if (content is AcademicScheduleContent) {
                setState { copy(scheduleBottomSheetContent = content) }
                setSideEffect { CalendarSideEffect.ShowScheduleBottomSheet }
            } else {
                setSideEffect { CalendarSideEffect.ShowLoginDialog }
            }
        }
    }

    private fun showScheduleBottomSheetById(scheduleId: Long) {
        when (loginManager.loginStatus.value) {
            is LoginStatus.Login -> {
                val schedule =
                    state.value.schedules
                        .filterIsInstance<PersonalScheduleUiModel>()
                        .find { it.id == scheduleId }

                if (schedule != null) {
                    navigateToDate(schedule.endAt.toLocalDate())
                    showScheduleBottomSheet(schedule)
                    pendingOpenScheduleId = null
                } else {
                    pendingOpenScheduleId = scheduleId
                }
            }

            LoginStatus.Logout, LoginStatus.Uninitialized, LoginStatus.NetworkDisconnected, LoginStatus.LoginInProgress -> {
                pendingOpenScheduleId = scheduleId
            }
        }
    }

    private fun navigateToDate(date: LocalDate) {
        val dateYearMonth = YearMonth(year = date.year, month = date.monthValue)
        val currentYearMonth = state.value.currentYearMonth

        if (dateYearMonth != currentYearMonth) {
            val baseToday = scheduleManager.baseToday
            val baseTodayYearMonth =
                YearMonth(year = baseToday.year, month = baseToday.monthValue)
            val monthsDiff =
                (dateYearMonth.year - baseTodayYearMonth.year) * 12 +
                        (dateYearMonth.month - baseTodayYearMonth.month)

            scheduleManager.updateSelectedDate(date)
            setState { copy(selectedDate = date, currentYearMonth = dateYearMonth) }
            setSideEffect { CalendarSideEffect.ScrollToPage(monthsDiff) }
        } else {
            scheduleManager.updateSelectedDate(date)
            setState { copy(selectedDate = date) }
        }
    }

    companion object {
        private const val KEY_PENDING_OPEN_SCHEDULE_ID = "pending_open_schedule_id"
    }
}
