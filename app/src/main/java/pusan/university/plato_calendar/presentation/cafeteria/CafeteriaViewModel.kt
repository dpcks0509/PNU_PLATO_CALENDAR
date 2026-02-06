package pusan.university.plato_calendar.presentation.cafeteria

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.data.local.database.CafeteriaDataStore
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.NextDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.PreviousDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.Refresh
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectCafeteria
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaSideEffect
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel
@Inject
constructor(
    private val loadingManager: LoadingManager,
    private val cafeteriaRepository: CafeteriaRepository,
    private val cafeteriaDataStore: CafeteriaDataStore,
    private val scheduleManager: ScheduleManager,
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
    }

    override suspend fun handleEvent(event: CafeteriaEvent) {
        when (event) {
            is SelectCafeteria -> {
                cafeteriaDataStore.setSelectedCafeteria(event.cafeteria)
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

            Refresh -> refresh()
        }
    }

    init {
        observeSelectedCafeteria()
    }

    private fun observeSelectedCafeteria() {
        viewModelScope.launch {
            cafeteriaDataStore.selectedCafeteria.collect { selectedCafeteria ->
                setState { copy(selectedCafeteria = selectedCafeteria) }
                getCafeteriaWeeklyPlan(selectedCafeteria)
            }
        }
    }

    private fun getCafeteriaWeeklyPlan(cafeteria: Cafeteria, refresh: Boolean = false) {
        viewModelScope.launch {
            if (!refresh && state.value.cafeteriaWeeklyPlans.containsKey(cafeteria)) {
                return@launch
            }

            loadingManager.updateLoading(true)

            cafeteriaRepository
                .getCafeteriaWeeklyPlan(cafeteria = cafeteria)
                .onSuccess { weeklyPlan ->
                    setState {
                        val newPlans = if (refresh) {
                            mapOf(cafeteria to weeklyPlan)
                        } else {
                            cafeteriaWeeklyPlans + (cafeteria to weeklyPlan)
                        }

                        copy(cafeteriaWeeklyPlans = newPlans)
                    }
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }

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
