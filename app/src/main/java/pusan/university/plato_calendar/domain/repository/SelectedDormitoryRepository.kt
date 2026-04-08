package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.domain.entity.Dormitory

interface SelectedDormitoryRepository {
    fun getSelectedDormitory(): Flow<Dormitory>

    suspend fun setSelectedDormitory(dormitory: Dormitory)
}
