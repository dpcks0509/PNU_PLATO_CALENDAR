package pusan.university.plato_calendar.data.local.repository

import kotlinx.coroutines.flow.Flow
import pusan.university.plato_calendar.data.local.database.CompletedScheduleDataStore
import pusan.university.plato_calendar.domain.repository.CompletedScheduleRepository
import javax.inject.Inject

class LocalCompletedScheduleRepository
    @Inject
    constructor(
        private val completedScheduleDataStore: CompletedScheduleDataStore,
    ) : CompletedScheduleRepository {
        override fun getCompletedScheduleIds(): Flow<Set<Long>> =
            completedScheduleDataStore.completedScheduleIds

        override suspend fun addCompletedSchedule(id: Long) {
            completedScheduleDataStore.addCompletedSchedule(id)
        }

        override suspend fun removeCompletedSchedule(id: Long) {
            completedScheduleDataStore.removeCompletedSchedule(id)
        }
    }
