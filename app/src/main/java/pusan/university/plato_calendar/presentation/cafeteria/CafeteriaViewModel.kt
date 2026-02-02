package pusan.university.plato_calendar.presentation.cafeteria

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.data.local.database.CafeteriaDataStore
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.repository.CafeteriaRepository
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectCafeteria
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaSideEffect
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
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
        override suspend fun handleEvent(event: CafeteriaEvent) {
            when (event) {
                is SelectCafeteria -> {
                    cafeteriaDataStore.setSelectedCafeteria(event.cafeteria)
                }

                is CafeteriaEvent.PreviousDay -> {
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

                is CafeteriaEvent.NextDay -> {
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
            }
        }

        init {
            observeSelectedCafeteria()
            getCafeteriaWeeklyPlan()
        }

        private fun observeSelectedCafeteria() {
            viewModelScope.launch {
                cafeteriaDataStore.selectedCafeteria.collect { selectedCafeteria ->
                    setState { copy(selectedCafeteria = selectedCafeteria) }
                }
            }
        }

        private fun getCafeteriaWeeklyPlan() {
            viewModelScope.launch {
                loadingManager.updateLoading(true)

                val results =
                    Cafeteria.entries
                        .map { cafeteria ->
                            async {
                                cafeteriaRepository.getCafeteriaWeeklyPlan(cafeteria = cafeteria)
                            }
                        }.awaitAll()

                var isErrorNotified = false

                val cafeteriaWeeklyPlans =
                    results
                        .mapNotNull { result ->
                            result
                                .onFailure { throwable ->
                                    if (!isErrorNotified) {
                                        ToastEventBus.sendError(throwable.message)
                                        isErrorNotified = true
                                    }
                                }.getOrNull()
                        }

                setState { copy(cafeteriaWeeklyPlans = cafeteriaWeeklyPlans) }
                loadingManager.updateLoading(false)
            }
        }
    }
