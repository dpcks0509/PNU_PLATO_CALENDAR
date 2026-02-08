package pusan.university.plato_calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException.Companion.NETWORK_ERROR_MESSAGE
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
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
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.TryLogin
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarSideEffect
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.calendar.model.DaySchedule
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.YearMonth
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
    private val scheduleRepository: ScheduleRepository,
    private val courseRepository: CourseRepository,
    private val scheduleManager: ScheduleManager,
    private val loadingManager: LoadingManager,
    private val alarmScheduler: AlarmScheduler,
) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(
    initialState =
        CalendarState(
            today = scheduleManager.today.value.toLocalDate(),
            selectedDate = scheduleManager.today.value.toLocalDate(),
        ),
) {
    private var pendingOpenScheduleId: Long? = null

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

            is TryLogin -> {
                val isLoginSuccess = loginManager.login(event.loginCredentials)
                if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }
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

            is UpdateCurrentYearMonth -> {
                setState { copy(currentYearMonth = event.yearMonth) }
            }

            is ShowScheduleBottomSheet -> {
                if (state.value.isScheduleBottomSheetVisible) {
                    setState {
                        copy(
                            scheduleBottomSheetContent = null,
                            isScheduleBottomSheetVisible = false,
                        )
                    }
                    delay(300)
                }

                showScheduleBottomSheet(schedule = event.schedule)
            }

            is ShowScheduleBottomSheetById -> {
                if (state.value.isScheduleBottomSheetVisible) {
                    setState {
                        copy(
                            scheduleBottomSheetContent = null,
                            isScheduleBottomSheetVisible = false,
                        )
                    }
                    delay(300)
                }

                showScheduleBottomSheetById(scheduleId = event.scheduleId)
            }

            HideScheduleBottomSheet -> {
                setState {
                    copy(
                        scheduleBottomSheetContent = null,
                        isScheduleBottomSheetVisible = false,
                    )
                }
            }

            HideLoginDialog -> {
                setState { copy(isLoginDialogVisible = false) }
            }
        }
    }

    private fun refresh() {
        scheduleManager.updateToday()
        getSchedules()
    }

    fun getMonthSchedule(yearMonth: YearMonth): List<List<DaySchedule?>> =
        scheduleManager.getMonthSchedule(yearMonth)

    private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> {
        scheduleRepository
            .getAcademicSchedules()
            .onSuccess {
                val academicSchedules =
                    it
                        .map(::AcademicScheduleUiModel)
                        .toSet()
                        .toList()

                return academicSchedules
            }.onFailure { throwable ->
                if (throwable !is NoNetworkConnectivityException && throwable !is CancellationException) {
                    ToastEventBus.sendError(
                        throwable.message,
                    )
                }
            }

        return emptyList()
    }

    private suspend fun getPersonalSchedules(sessKey: String): List<ScheduleUiModel> {
        scheduleRepository
            .getPersonalSchedules(sessKey = sessKey)
            .onSuccess {
                val personalSchedules =
                    it.map { domain ->
                        when (domain) {
                            is CourseSchedule -> {
                                val courseName =
                                    courseRepository.getCourseName(
                                        domain.courseCode,
                                    )

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

                return personalSchedules
            }.onFailure { throwable ->
                loadingManager.updateLoading(false)

                if (throwable !is CancellationException) {
                    ToastEventBus.sendError(throwable.message)
                }
            }

        return emptyList()
    }

    private fun getSchedules() {
        viewModelScope.launch {
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
                            showScheduleBottomSheet(targetSchedule)
                            pendingOpenScheduleId = null
                        }
                    }
                }

                LoginStatus.Logout -> {
                    loadingManager.updateLoading(true)

                    val academicSchedules = getAcademicSchedules()

                    scheduleManager.updateSchedules(academicSchedules)
                    loadingManager.updateLoading(false)
                }

                LoginStatus.Uninitialized -> {
                    loadingManager.updateLoading(false)
                }

                LoginStatus.NetworkDisconnected -> {
                    ToastEventBus.sendError(NETWORK_ERROR_MESSAGE)
                    loadingManager.updateLoading(false)
                }

                LoginStatus.LoginInProgress -> Unit
            }
        }
    }

    private suspend fun makeCustomSchedule(newSchedule: NewSchedule) {
        scheduleRepository
            .makeCustomSchedule(newSchedule)
            .onSuccess { id ->
                val customSchedule =
                    CustomScheduleUiModel(
                        id = id,
                        title = newSchedule.title,
                        description = newSchedule.description,
                        startAt = newSchedule.startAt,
                        endAt = newSchedule.endAt,
                        isCompleted = false,
                    )
                val updatedSchedules = state.value.schedules + customSchedule
                scheduleManager.updateSchedules(updatedSchedules)

                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 생성되었습니다.")
            }.onFailure { throwable ->
                ToastEventBus.sendError(throwable.message)
            }
    }

    private suspend fun editCustomSchedule(customSchedule: CustomSchedule) {
        scheduleRepository
            .editPersonalSchedule(customSchedule)
            .onSuccess {
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

                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 수정되었습니다.")
            }.onFailure { throwable ->
                ToastEventBus.sendError(throwable.message)
            }
    }

    private suspend fun deleteCustomSchedule(id: Long) {
        scheduleRepository
            .deleteCustomSchedule(id)
            .onSuccess {
                scheduleRepository.markScheduleAsUncompleted(id)

                val updatedSchedules =
                    state.value.schedules.filter { schedule ->
                        !(schedule is PersonalScheduleUiModel && schedule.id == id)
                    }
                scheduleManager.updateSchedules(updatedSchedules)
                alarmScheduler.cancelNotification(id)

                setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                ToastEventBus.sendSuccess("일정이 삭제되었습니다.")
            }.onFailure { throwable ->
                ToastEventBus.sendError(throwable.message)
            }
    }

    private suspend fun togglePersonalScheduleCompletion(
        id: Long,
        isCompleted: Boolean,
    ) {
        if (isCompleted) {
            scheduleRepository.markScheduleAsCompleted(id)
        } else {
            scheduleRepository.markScheduleAsUncompleted(id)
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

        setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
        ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
    }

    private fun showScheduleBottomSheet(schedule: ScheduleUiModel?) {
        val bottomSheetContent =
            when (schedule) {
                is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                is AcademicScheduleUiModel -> AcademicScheduleContent(schedule)
                null -> NewScheduleContent
            }

        if (loginManager.loginStatus.value is LoginStatus.Login) {
            setState {
                copy(
                    scheduleBottomSheetContent = bottomSheetContent,
                    isScheduleBottomSheetVisible = true,
                )
            }
        } else {
            if (bottomSheetContent is AcademicScheduleContent) {
                setState {
                    copy(
                        scheduleBottomSheetContent = bottomSheetContent,
                        isScheduleBottomSheetVisible = true,
                    )
                }
            } else {
                setState { copy(isLoginDialogVisible = true) }
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
                    showScheduleBottomSheet((schedule))
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

    suspend fun tryLogin(credentials: LoginCredentials): Boolean {
        val isLoginSuccess = loginManager.login(credentials)
        if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }
        return isLoginSuccess
    }
}
