package pusan.university.plato_calendar.data.local.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.CafeteriaDataStore
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.repository.SelectedCafeteriaRepository
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import javax.inject.Inject

class LocalSelectedCafeteriaRepository
    @Inject
    constructor(
        private val cafeteriaDataStore: CafeteriaDataStore,
    ) : SelectedCafeteriaRepository {
        override fun getSelectedCafeteria(): Flow<Cafeteria> = cafeteriaDataStore.selectedCafeteria

        override suspend fun setSelectedCafeteria(cafeteria: Cafeteria) {
            cafeteriaDataStore.setSelectedCafeteria(cafeteria)
        }

        override fun getSelectedTab(): Flow<CafeteriaTab> = cafeteriaDataStore.selectedTab

        override suspend fun setSelectedTab(tab: CafeteriaTab) {
            cafeteriaDataStore.setSelectedTab(tab)
        }
    }
