package pusan.university.plato_calendar.presentation.cafeteria

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetCafeteriaWeeklyPlanUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.NextDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.PreviousDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.Refresh
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectCafeteria
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaSideEffect
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.util.base.BaseViewModel
import pusan.university.plato_calendar.presentation.util.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.util.manager.LoadingManager
import pusan.university.plato_calendar.presentation.util.manager.ScheduleManager
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel
@Inject
constructor(
    private val loadingManager: LoadingManager,
    private val scheduleManager: ScheduleManager,
    private val getCafeteriaWeeklyPlanUseCase: GetCafeteriaWeeklyPlanUseCase,
    private val getSelectedCafeteriaUseCase: GetSelectedCafeteriaUseCase,
    private val setSelectedCafeteriaUseCase: SetSelectedCafeteriaUseCase,
) : BaseViewModel<CafeteriaState, CafeteriaEvent, CafeteriaSideEffect>(
    initialState =
        CafeteriaState(
            today = scheduleManager.today.value.toLocalDate(),
            selectedDate = scheduleManager.today.value.toLocalDate(),
        ),
) {
    init {
        viewModelScope.launch {
            scheduleManager.today.collect { today ->
                setState { copy(today = today.toLocalDate()) }
            }
        }

        observeSelectedCafeteria()
    }

    override suspend fun handleEvent(event: CafeteriaEvent) {
        when (event) {
            is SelectCafeteria -> {
                setSelectedCafeteriaUseCase(event.cafeteria)
            }

            PreviousDay -> {
                setState {
                    val weekStartDate = getWeekStartDate()
                    val newDate = selectedDate.minusDays(1)

                    if (weekStartDate != null && !newDate.isBefore(weekStartDate)) {
                        copy(selectedDate = newDate)
                    } else {
                        this
                    }
                }
            }

            NextDay -> {
                setState {
                    val weekEndDate = getWeekEndDate()
                    val newDate = selectedDate.plusDays(1)

                    if (weekEndDate != null && !newDate.isAfter(weekEndDate)) {
                        copy(selectedDate = newDate)
                    } else {
                        this
                    }
                }
            }

            Refresh -> {
                refresh()
            }
        }
    }

    private fun observeSelectedCafeteria() {
        viewModelScope.launch {
            getSelectedCafeteriaUseCase().collect { selectedCafeteria ->
                setState { copy(selectedCafeteria = selectedCafeteria) }
                getCafeteriaWeeklyPlan(selectedCafeteria)
            }
        }
    }

    private fun getCafeteriaWeeklyPlan(
        cafeteria: Cafeteria,
        refresh: Boolean = false,
    ) {
        viewModelScope
            .launch {
                if (!refresh && state.value.cafeteriaWeeklyPlans.containsKey(cafeteria)) {
                    return@launch
                }

                loadingManager.updateLoading(true)

                when (val result = getCafeteriaWeeklyPlanUseCase(cafeteria)) {
                    is ApiResult.Success -> {
                        setState {
                            val newPlans =
                                if (refresh) {
                                    mapOf(cafeteria to result.data)
                                } else {
                                    cafeteriaWeeklyPlans + (cafeteria to result.data)
                                }

                            copy(cafeteriaWeeklyPlans = newPlans)
                        }
                    }

                    is ApiResult.Error -> {
                        ToastEventBus.sendError(result.exception.message)
                    }
                }
            }.invokeOnCompletion {
                loadingManager.updateLoading(false)
            }
    }

    private fun refresh() {
        val previousTodayDate = state.value.today
        scheduleManager.updateToday()
        val newTodayDate = state.value.today

        if (previousTodayDate != newTodayDate && newTodayDate.dayOfWeek == DayOfWeek.SUNDAY) {
            setState { copy(selectedDate = newTodayDate) }
        }

        getCafeteriaWeeklyPlan(cafeteria = state.value.selectedCafeteria, refresh = true)
    }
}
