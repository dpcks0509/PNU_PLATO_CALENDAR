package pusan.university.plato_calendar.presentation.todo

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException.Companion.NETWORK_ERROR_MESSAGE
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.CourseRepository
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.DeleteCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.EditCustomSchedule
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.HideScheduleBottomSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.Refresh
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.ShowScheduleBottomSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect
import pusan.university.plato_calendar.presentation.todo.intent.ToDoState
import javax.inject.Inject
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect.HideScheduleBottomSheet as ToDoHideSheet

@HiltViewModel
class ToDoViewModel
@Inject
constructor(
    private val scheduleManager: ScheduleManager,
    private val loadingManager: LoadingManager,
    private val scheduleRepository: ScheduleRepository,
    private val courseRepository: CourseRepository,
    private val loginManager: LoginManager,
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
        }
    }

    override suspend fun handleEvent(event: ToDoEvent) {
        when (event) {
            is Refresh -> {
                refresh()
            }

            is TogglePersonalScheduleCompletion -> {
                togglePersonalScheduleCompletion(
                    event.id,
                    event.isCompleted,
                )
            }

            is ShowScheduleBottomSheet -> {
                showScheduleBottomSheet(event)
            }

            is HideScheduleBottomSheet -> {
                hideScheduleBottomSheet()
            }

            is EditCustomSchedule -> {
                editCustomSchedule(event.schedule)
            }

            is DeleteCustomSchedule -> {
                deleteCustomSchedule(event.id)
            }
        }
    }

    private fun refresh() {
        scheduleManager.updateToday()
        getSchedules()
    }

    private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> {
        scheduleRepository
            .getAcademicSchedules()
            .onSuccess {
                val academicSchedules = it.map(::AcademicScheduleUiModel)

                return academicSchedules
            }.onFailure { throwable ->
                if (throwable !is NoNetworkConnectivityException) ToastEventBus.sendError(throwable.message)
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

                ToastEventBus.sendError(throwable.message)
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

        setSideEffect { ToDoHideSheet }
        ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
    }

    private fun showScheduleBottomSheet(event: ShowScheduleBottomSheet) {
        val content: ScheduleBottomSheetContent =
            when (val schedule = event.schedule) {
                is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                is AcademicScheduleUiModel -> AcademicScheduleContent(schedule)
                null -> ScheduleBottomSheetContent.NewScheduleContent
            }

        setState {
            copy(
                scheduleBottomSheetContent = content,
                isScheduleBottomSheetVisible = true,
            )
        }
    }

    private fun hideScheduleBottomSheet() {
        setState {
            copy(
                scheduleBottomSheetContent = null,
                isScheduleBottomSheetVisible = false,
            )
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

                setSideEffect { ToDoHideSheet }
                ToastEventBus.sendSuccess("일정이 수정되었습니다.")
            }.onFailure { throwable ->
                ToastEventBus.sendError(throwable.message)
            }
    }

    private suspend fun deleteCustomSchedule(id: Long) {
        scheduleRepository
            .deleteCustomSchedule(id)
            .onSuccess {
                val updatedSchedules =
                    state.value.schedules.filter { schedule ->
                        !(schedule is PersonalScheduleUiModel && schedule.id == id)
                    }
                scheduleManager.updateSchedules(updatedSchedules)

                setSideEffect { ToDoHideSheet }
                ToastEventBus.sendSuccess("일정이 삭제되었습니다.")
            }.onFailure { throwable ->
                ToastEventBus.sendError(throwable.message)
            }
    }
}
