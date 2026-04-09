package pusan.university.plato_calendar.presentation.cafeteria

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetCafeteriaWeeklyPlanUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetDormitoryCafeteriaWeeklyPlanUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedCafeteriaTabUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedDormitoryUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedCafeteriaTabUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedCafeteriaUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedDormitoryUseCase
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.NextDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.PreviousDay
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.Refresh
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectCafeteria
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectDormitory
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectTab
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaSideEffect
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
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
    private val getSelectedCafeteriaTabUseCase: GetSelectedCafeteriaTabUseCase,
    private val setSelectedCafeteriaTabUseCase: SetSelectedCafeteriaTabUseCase,
    private val getCafeteriaWeeklyPlanUseCase: GetCafeteriaWeeklyPlanUseCase,
    private val getDormitoryCafeteriaWeeklyPlanUseCase: GetDormitoryCafeteriaWeeklyPlanUseCase,
    private val getSelectedCafeteriaUseCase: GetSelectedCafeteriaUseCase,
    private val setSelectedCafeteriaUseCase: SetSelectedCafeteriaUseCase,
    private val getSelectedDormitoryUseCase: GetSelectedDormitoryUseCase,
    private val setSelectedDormitoryUseCase: SetSelectedDormitoryUseCase,
) : BaseViewModel<CafeteriaState, CafeteriaEvent, CafeteriaSideEffect>(
    initialState =
        CafeteriaState(
            today = scheduleManager.today.value.toLocalDate(),
            selectedDate = scheduleManager.today.value.toLocalDate(),
        ),
) {
    init {
        viewModelScope.launch {
            val initialTab = getSelectedCafeteriaTabUseCase().first()
            setState { copy(selectedTab = initialTab) }
        }

        viewModelScope.launch {
            scheduleManager.today.collect { today ->
                setState { copy(today = today.toLocalDate()) }
            }
        }

        observeSelectedCafeteria()
        observeSelectedDormitory()
    }

    override suspend fun handleEvent(event: CafeteriaEvent) {
        when (event) {
            is SelectCafeteria -> {
                setSelectedCafeteriaUseCase(event.cafeteria)
            }

            is SelectDormitory -> {
                setSelectedDormitoryUseCase(event.dormitory)
            }

            is SelectTab -> {
                setState { copy(selectedTab = event.tab) }
                setSelectedCafeteriaTabUseCase(event.tab)
                when (event.tab) {
                    CafeteriaTab.DORMITORY -> getDormitoryWeeklyPlan(state.value.selectedDormitory)
                    CafeteriaTab.CAMPUS -> getCafeteriaWeeklyPlan(state.value.selectedCafeteria)
                }
            }

            PreviousDay -> {
                setState {
                    val weekStartDate = getWeekStartDate()
                    val newDate = selectedDate.minusDays(1)
                    if (weekStartDate != null && !newDate.isBefore(weekStartDate)) copy(selectedDate = newDate) else this
                }
            }

            NextDay -> {
                setState {
                    val weekEndDate = getWeekEndDate()
                    val newDate = selectedDate.plusDays(1)
                    if (weekEndDate != null && !newDate.isAfter(weekEndDate)) copy(selectedDate = newDate) else this
                }
            }

            Refresh -> {
                refresh(state.value.selectedTab)
            }
        }
    }

    private fun observeSelectedCafeteria() {
        viewModelScope.launch {
            getSelectedCafeteriaUseCase().distinctUntilChanged().collect { selectedCafeteria ->
                setState { copy(selectedCafeteria = selectedCafeteria) }
                if (state.value.selectedTab == CafeteriaTab.CAMPUS) {
                    getCafeteriaWeeklyPlan(selectedCafeteria)
                }
            }
        }
    }

    private fun observeSelectedDormitory() {
        viewModelScope.launch {
            getSelectedDormitoryUseCase().distinctUntilChanged().collect { selectedDormitory ->
                setState { copy(selectedDormitory = selectedDormitory) }
                if (state.value.selectedTab == CafeteriaTab.DORMITORY) {
                    getDormitoryWeeklyPlan(selectedDormitory)
                }
            }
        }
    }

    private fun getCafeteriaWeeklyPlan(cafeteria: Cafeteria, refresh: Boolean = false) {
        viewModelScope.launch {
            if (!refresh && state.value.cafeteriaWeeklyPlans.containsKey(cafeteria)) return@launch

            val isActiveTab = state.value.selectedTab == CafeteriaTab.CAMPUS
            if (isActiveTab) loadingManager.updateLoading(true)

            when (val result = getCafeteriaWeeklyPlanUseCase(cafeteria)) {
                is ApiResult.Success -> {
                    setState {
                        val newPlans = if (refresh) mapOf(cafeteria to result.data)
                        else cafeteriaWeeklyPlans + (cafeteria to result.data)
                        copy(cafeteriaWeeklyPlans = newPlans)
                    }
                }

                is ApiResult.Error -> ToastEventBus.sendError(result.exception.message)
            }
        }.invokeOnCompletion {
            if (state.value.selectedTab == CafeteriaTab.CAMPUS) loadingManager.updateLoading(false)
        }
    }

    private fun getDormitoryWeeklyPlan(dormitory: Dormitory, refresh: Boolean = false) {
        viewModelScope.launch {
            if (!refresh && state.value.dormWeeklyPlans.containsKey(dormitory)) return@launch

            val isActiveTab = state.value.selectedTab == CafeteriaTab.DORMITORY
            if (isActiveTab) loadingManager.updateLoading(true)

            delay(300)

            when (val result = getDormitoryCafeteriaWeeklyPlanUseCase(dormitory)) {
                is ApiResult.Success -> setState {
                    val newPlans = if (refresh) mapOf(dormitory to result.data)
                    else dormWeeklyPlans + (dormitory to result.data)
                    copy(dormWeeklyPlans = newPlans)
                }

                is ApiResult.Error -> ToastEventBus.sendError(result.exception.message)
            }
        }.invokeOnCompletion {
            if (state.value.selectedTab == CafeteriaTab.DORMITORY) loadingManager.updateLoading(false)
        }
    }

    private fun refresh(tab: CafeteriaTab?) {
        val previousTodayDate = state.value.today
        scheduleManager.updateToday()
        val newTodayDate = state.value.today

        if (previousTodayDate != newTodayDate && newTodayDate.dayOfWeek == DayOfWeek.SUNDAY) {
            setState { copy(selectedDate = newTodayDate) }
        }

        if (tab == CafeteriaTab.CAMPUS) {
            getCafeteriaWeeklyPlan(cafeteria = state.value.selectedCafeteria, refresh = true)
        } else {
            getDormitoryWeeklyPlan(dormitory = state.value.selectedDormitory, refresh = true)
        }
    }
}
