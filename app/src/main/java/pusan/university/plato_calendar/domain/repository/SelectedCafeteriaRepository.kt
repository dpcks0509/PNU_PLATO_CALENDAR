package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.Cafeteria

interface SelectedCafeteriaRepository {
    fun getSelectedCafeteria(): Flow<Cafeteria>

    suspend fun setSelectedCafeteria(cafeteria: Cafeteria)
}
