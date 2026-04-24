package pusan.university.plato_calendar.presentation.todo

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException.Companion.NETWORK_ERROR_MESSAGE
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.AcademicScheduleAlarmInfo
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.entity.ScheduleAlarmInfo
import pusan.university.plato_calendar.domain.usecase.course.GetCourseNameUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.DeleteCustomScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.EditPersonalScheduleUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAcademicScheduleAlarmInfoUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAcademicSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAllAcademicScheduleAlarmInfosUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetAllScheduleAlarmInfosUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetPersonalSchedulesUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.GetScheduleAlarmInfoUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.SaveAcademicScheduleAlarmInfoUseCase
import pusan.university.plato_calendar.domain.usecase.schedule.SaveScheduleAlarmInfoUseCase
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.setting.model.AcademicNotificationHour
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.DeleteCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.EditCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.Refresh
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.ShowScheduleBottomSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect
import pusan.university.plato_calendar.presentation.todo.intent.ToDoState
import pusan.university.plato_calendar.presentation.util.base.BaseViewModel
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.util.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.util.manager.LoadingManager
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import pusan.university.plato_calendar.presentation.util.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.util.manager.SettingsManager
import pusan.university.plato_calendar.presentation.util.notification.AlarmScheduler
import java.io.IOException
import javax.inject.Inject
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect.HideScheduleBottomSheet as ToDoHideSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect.ShowScheduleBottomSheet as ToDoShowSheet

@HiltViewModel
class ToDoViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
    private val scheduleManager: ScheduleManager,
    private val loadingManager: LoadingManager,
    private val settingsManager: SettingsManager,
    private val alarmScheduler: AlarmScheduler,
    private val saveScheduleAlarmInfoUseCase: SaveScheduleAlarmInfoUseCase,
    private val getAllScheduleAlarmInfosUseCase: GetAllScheduleAlarmInfosUseCase,
    private val getScheduleAlarmInfoUseCase: GetScheduleAlarmInfoUseCase,
    private val getAcademicSchedulesUseCase: GetAcademicSchedulesUseCase,
    private val getPersonalSchedulesUseCase: GetPersonalSchedulesUseCase,
    private val getCourseNameUseCase: GetCourseNameUseCase,
    private val editPersonalScheduleUseCase: EditPersonalScheduleUseCase,
    private val deleteCustomScheduleUseCase: DeleteCustomScheduleUseCase,
    private val saveAcademicScheduleAlarmInfoUseCase: SaveAcademicScheduleAlarmInfoUseCase,
    private val getAcademicScheduleAlarmInfoUseCase: GetAcademicScheduleAlarmInfoUseCase,
    private val getAllAcademicScheduleAlarmInfosUseCase: GetAllAcademicScheduleAlarmInfosUseCase,
) : BaseViewModel<ToDoState, ToDoEvent, ToDoSideEffect>(
    initialState = ToDoState(today = scheduleManager.today.value),
) {
    val today get() = scheduleManager.today

    init {
        viewModelScope.launch {
            launch {
                scheduleManager.today.collect { today ->
                    setState { copy(today = today) }
                }
            }

            launch {
                scheduleManager.schedules.collect { schedules ->
                    setState { copy(schedules = schedules) }
                }
            }

            launch {
                settingsManager.appSettings.collect { settings ->
                    setState {
                        copy(
                            defaultFirstReminderTime = settings.firstReminderTime,
                            defaultSecondReminderTime = settings.secondReminderTime,
                        )
                    }
                }
            }
        }
    }

    override suspend fun handleEvent(event: ToDoEvent) {
        when (event) {
            is Refresh -> {
                refresh()
            }

            is TogglePersonalScheduleCompletion -> {
                togglePersonalScheduleCompletion(
                    id = event.id,
                    isCompleted = event.isCompleted,
                )
            }

            is ShowScheduleBottomSheet -> {
                showScheduleBottomSheet(schedule = event.schedule)
            }

            is EditCustomSchedule -> {
                editCustomSchedule(customSchedule = event.customSchedule)
            }

            is DeleteCustomSchedule -> {
                deleteCustomSchedule(id = event.id)
            }

            is ToDoEvent.ShowDialog -> {
                setState { copy(scheduleDialogContent = event.content) }
            }

            ToDoEvent.HideDialog -> {
                setState { copy(scheduleDialogContent = null) }
            }

            is ToDoEvent.UpdateScheduleAlarm -> {
                val currentSettings = settingsManager.appSettings.first()
                val isCustomized =
                    event.firstReminderTime != currentSettings.firstReminderTime ||
                            event.secondReminderTime != currentSettings.secondReminderTime
                val alarmInfo = ScheduleAlarmInfo(
                    notificationsEnabled = event.enabled,
                    firstReminderTime = event.firstReminderTime,
                    secondReminderTime = event.secondReminderTime,
                    isCustomized = isCustomized,
                )
                saveScheduleAlarmInfoUseCase(event.scheduleId, alarmInfo)

                val updatedSchedules = state.value.schedules.map { s ->
                    if (s is PersonalScheduleUiModel && s.id == event.scheduleId) {
                        when (s) {
                            is CourseScheduleUiModel -> s.copy(
                                notificationsEnabled = event.enabled,
                                firstReminderTime = event.firstReminderTime,
                                secondReminderTime = event.secondReminderTime,
                                isCustomized = isCustomized,
                            )

                            is CustomScheduleUiModel -> s.copy(
                                notificationsEnabled = event.enabled,
                                firstReminderTime = event.firstReminderTime,
                                secondReminderTime = event.secondReminderTime,
                                isCustomized = isCustomized,
                            )
                        }
                    } else s
                }
                scheduleManager.updateSchedules(updatedSchedules)

                val schedule = state.value.schedules
                    .filterIsInstance<PersonalScheduleUiModel>()
                    .find { it.id == event.scheduleId }
                if (schedule != null) {
                    if (event.enabled) {
                        alarmScheduler.scheduleNotificationsForSchedule(
                            schedule = schedule,
                            firstReminderTime = event.firstReminderTime,
                            secondReminderTime = event.secondReminderTime,
                        )
                    } else {
                        alarmScheduler.cancelNotification(event.scheduleId)
                    }
                }
            }

            is ToDoEvent.UpdateAcademicScheduleAlarm -> {
                val schedule = event.schedule
                val key = AcademicScheduleAlarmInfo.generateKey(schedule.title, schedule.startAt, schedule.endAt)
                val notificationsEnabled = event.enabled &&
                    (event.startDateHour != AcademicNotificationHour.NONE || event.endDateHour != AcademicNotificationHour.NONE)

                saveAcademicScheduleAlarmInfoUseCase(
                    key,
                    AcademicScheduleAlarmInfo(
                        title = schedule.title,
                        startAt = schedule.startAt,
                        endAt = schedule.endAt,
                        notificationsEnabled = event.enabled,
                        startDateHour = event.startDateHour,
                        endDateHour = event.endDateHour,
                    ),
                )

                val updatedSchedules = state.value.schedules.map { s ->
                    if (s is AcademicScheduleUiModel && s.title == schedule.title && s.startAt == schedule.startAt && s.endAt == schedule.endAt) {
                        s.copy(notificationsEnabled = notificationsEnabled)
                    } else {
                        s
                    }
                }
                scheduleManager.updateSchedules(updatedSchedules)

                if (event.enabled) {
                    alarmScheduler.scheduleAcademicNotification(
                        title = schedule.title,
                        startAt = schedule.startAt,
                        endAt = schedule.endAt,
                        startDateHour = event.startDateHour,
                        endDateHour = event.endDateHour,
                    )
                } else {
                    alarmScheduler.cancelAcademicNotification(key)
                }
            }
        }
    }

    fun hideBottomSheet() {
        setState { copy(scheduleBottomSheetContent = null) }
    }

    private fun List<ScheduleUiModel>.withAlarmInfos(alarmInfos: Map<Long, ScheduleAlarmInfo>): List<ScheduleUiModel> =
        map { schedule ->
            if (schedule is PersonalScheduleUiModel) {
                val info = alarmInfos[schedule.id] ?: return@map schedule
                when (schedule) {
                    is CourseScheduleUiModel -> schedule.copy(
                        isCompleted = info.isCompleted,
                        notificationsEnabled = info.notificationsEnabled,
                        firstReminderTime = info.firstReminderTime,
                        secondReminderTime = info.secondReminderTime,
                        isCustomized = info.isCustomized,
                    )

                    is CustomScheduleUiModel -> schedule.copy(
                        isCompleted = info.isCompleted,
                        notificationsEnabled = info.notificationsEnabled,
                        firstReminderTime = info.firstReminderTime,
                        secondReminderTime = info.secondReminderTime,
                        isCustomized = info.isCustomized,
                    )
                }
            } else {
                schedule
            }
        }

    private fun List<ScheduleUiModel>.withAcademicAlarmInfos(
        academicAlarmInfos: List<AcademicScheduleAlarmInfo>,
    ): List<ScheduleUiModel> {
        val alarmMap = academicAlarmInfos.associateBy {
            AcademicScheduleAlarmInfo.generateKey(it.title, it.startAt, it.endAt)
        }
        return map { schedule ->
            if (schedule is AcademicScheduleUiModel) {
                val key = AcademicScheduleAlarmInfo.generateKey(schedule.title, schedule.startAt, schedule.endAt)
                val info = alarmMap[key] ?: return@map schedule
                schedule.copy(
                    notificationsEnabled = info.notificationsEnabled &&
                        (info.startDateHour != AcademicNotificationHour.NONE || info.endDateHour != AcademicNotificationHour.NONE),
                )
            } else {
                schedule
            }
        }
    }

    private fun refresh() {
        scheduleManager.updateToday()
        getSchedules()
    }

    private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> =
        when (val result = getAcademicSchedulesUseCase()) {
            is ApiResult.Success -> {
                result.data.map(::AcademicScheduleUiModel)
            }

            is ApiResult.Error -> {
                if (result.exception !is NoNetworkConnectivityException) {
                    ToastEventBus.sendError(result.exception.message)
                }
                scheduleManager.schedules.value.filterIsInstance<AcademicScheduleUiModel>()
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
                val isNetworkError = result.exception is NoNetworkConnectivityException || result.exception is IOException
                if (!isNetworkError) {
                    ToastEventBus.sendError(result.exception.message)
                }
                scheduleManager.schedules.value.filterIsInstance<PersonalScheduleUiModel>()
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

                        val alarmInfos = getAllScheduleAlarmInfosUseCase()
                        val academicAlarmInfos = getAllAcademicScheduleAlarmInfosUseCase()
                        val schedules = (academicSchedules + personalSchedules)
                            .withAlarmInfos(alarmInfos)
                            .withAcademicAlarmInfos(academicAlarmInfos)

                        if (schedules.isNotEmpty()) scheduleManager.updateSchedules(schedules)
                    }

                    LoginStatus.Logout -> {
                        loadingManager.updateLoading(true)

                        val academicSchedules = getAcademicSchedules()
                        val academicAlarmInfos = getAllAcademicScheduleAlarmInfosUseCase()

                        scheduleManager.updateSchedules(academicSchedules.withAcademicAlarmInfos(academicAlarmInfos))
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

    private suspend fun togglePersonalScheduleCompletion(
        id: Long,
        isCompleted: Boolean,
    ) {
        val freshAlarmInfo = getScheduleAlarmInfoUseCase(id)
        val updatedAlarmInfo = (freshAlarmInfo ?: ScheduleAlarmInfo()).copy(isCompleted = isCompleted)
        saveScheduleAlarmInfoUseCase(id, updatedAlarmInfo)

        if (isCompleted) {
            alarmScheduler.cancelNotification(id)
        } else if (updatedAlarmInfo.notificationsEnabled) {
            val schedule = state.value.schedules.filterIsInstance<PersonalScheduleUiModel>().find { it.id == id }
            if (schedule != null) {
                val currentSettings = settingsManager.appSettings.first()
                alarmScheduler.scheduleNotificationsForSchedule(
                    schedule = schedule,
                    firstReminderTime = if (updatedAlarmInfo.isCustomized) updatedAlarmInfo.firstReminderTime else currentSettings.firstReminderTime,
                    secondReminderTime = if (updatedAlarmInfo.isCustomized) updatedAlarmInfo.secondReminderTime else currentSettings.secondReminderTime,
                )
            }
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
        setSideEffect { ToDoHideSheet }
        ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
    }

    private suspend fun showScheduleBottomSheet(schedule: ScheduleUiModel?) {
        val content: ScheduleBottomSheetContent =
            when (schedule) {
                is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                is AcademicScheduleUiModel -> {
                    val key = AcademicScheduleAlarmInfo.generateKey(schedule.title, schedule.startAt, schedule.endAt)
                    val alarmInfo = getAcademicScheduleAlarmInfoUseCase(key)
                    AcademicScheduleContent(schedule, alarmInfo)
                }
                null -> ScheduleBottomSheetContent.NewScheduleContent
            }

        setState { copy(scheduleBottomSheetContent = content) }
        setSideEffect { ToDoShowSheet }
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
                setSideEffect { ToDoHideSheet }
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

                setState { copy(scheduleBottomSheetContent = null) }
                setSideEffect { ToDoHideSheet }
                ToastEventBus.sendSuccess("일정이 삭제되었습니다.")
            }

            is ApiResult.Error -> {
                ToastEventBus.sendError(result.exception.message)
            }
        }
    }
}
