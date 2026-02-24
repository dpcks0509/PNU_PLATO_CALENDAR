package pusan.university.plato_calendar.presentation.todo

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException.Companion.NETWORK_ERROR_MESSAGE
import pusan.university.plato_calendar.data.util.ApiResult
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
import javax.inject.Inject
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect.HideScheduleBottomSheet as ToDoHideSheet
import pusan.university.plato_calendar.presentation.todo.intent.ToDoSideEffect.ShowScheduleBottomSheet as ToDoShowSheet

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
            }
        }

        private fun refresh() {
            scheduleManager.updateToday()
            getSchedules()
        }

        private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> =
            when (val result = scheduleRepository.getAcademicSchedules()) {
                is ApiResult.Success -> {
                    result.data.map(::AcademicScheduleUiModel)
                }

                is ApiResult.Error -> {
                    if (result.exception !is NoNetworkConnectivityException) {
                        ToastEventBus.sendError(result.exception.message)
                    }
                    emptyList()
                }
            }

        private suspend fun getPersonalSchedules(sessKey: String): List<ScheduleUiModel> =
            when (val result = scheduleRepository.getPersonalSchedules(sessKey = sessKey)) {
                is ApiResult.Success -> {
                    result.data.map { domain ->
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

            setState { copy(scheduleBottomSheetContent = null) }
            setSideEffect { ToDoHideSheet }
            ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
        }

        private fun showScheduleBottomSheet(schedule: ScheduleUiModel?) {
            val content: ScheduleBottomSheetContent =
                when (schedule) {
                    is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                    is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                    is AcademicScheduleUiModel -> AcademicScheduleContent(schedule)
                    null -> ScheduleBottomSheetContent.NewScheduleContent
                }

            setState { copy(scheduleBottomSheetContent = content) }
            setSideEffect { ToDoShowSheet }
        }

        private suspend fun editCustomSchedule(customSchedule: CustomSchedule) {
            when (val result = scheduleRepository.editPersonalSchedule(customSchedule)) {
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
            when (val result = scheduleRepository.deleteCustomSchedule(id)) {
                is ApiResult.Success -> {
                    scheduleRepository.markScheduleAsUncompleted(id)

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
