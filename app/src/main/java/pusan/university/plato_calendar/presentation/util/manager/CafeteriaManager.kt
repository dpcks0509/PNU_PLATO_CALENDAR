package pusan.university.plato_calendar.presentation.util.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.domain.usecase.cafeteria.GetSelectedCafeteriaTabUseCase
import pusan.university.plato_calendar.domain.usecase.cafeteria.SetSelectedCafeteriaTabUseCase
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CafeteriaManager
@Inject
constructor(
    getSelectedCafeteriaTabUseCase: GetSelectedCafeteriaTabUseCase,
    private val setSelectedCafeteriaTabUseCase: SetSelectedCafeteriaTabUseCase,
) {
    val selectedTab: Flow<CafeteriaTab> = getSelectedCafeteriaTabUseCase()

    var initialTab: CafeteriaTab = CafeteriaTab.CAMPUS
        private set

    suspend fun loadInitialState() {
        initialTab = selectedTab.first()
    }

    suspend fun setSelectedTab(tab: CafeteriaTab) {
        setSelectedCafeteriaTabUseCase(tab)
    }
}
