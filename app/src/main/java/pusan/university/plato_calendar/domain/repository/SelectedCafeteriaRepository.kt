package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab

interface SelectedCafeteriaRepository {
    fun getSelectedCafeteria(): Flow<Cafeteria>

    suspend fun setSelectedCafeteria(cafeteria: Cafeteria)

    fun getSelectedTab(): Flow<CafeteriaTab>

    suspend fun setSelectedTab(tab: CafeteriaTab)
}
