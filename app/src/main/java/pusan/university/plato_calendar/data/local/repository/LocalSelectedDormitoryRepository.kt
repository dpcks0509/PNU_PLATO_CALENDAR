package pusan.university.plato_calendar.data.local.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.DormitoryDataStore
import pusan.university.plato_calendar.domain.entity.Dormitory
import pusan.university.plato_calendar.domain.repository.SelectedDormitoryRepository
import javax.inject.Inject

class LocalSelectedDormitoryRepository
@Inject
constructor(
    private val dormitoryDataStore: DormitoryDataStore,
) : SelectedDormitoryRepository {
    override fun getSelectedDormitory(): Flow<Dormitory> = dormitoryDataStore.selectedDormitory

    override suspend fun setSelectedDormitory(dormitory: Dormitory) {
        dormitoryDataStore.setSelectedDormitory(dormitory)
    }
}
