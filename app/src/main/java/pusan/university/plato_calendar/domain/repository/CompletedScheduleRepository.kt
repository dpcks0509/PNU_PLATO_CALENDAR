package pusan.university.plato_calendar.domain.repository

import kotlinx.coroutines.flow.Flow

interface CompletedScheduleRepository {
    fun getCompletedScheduleIds(): Flow<Set<Long>>

    suspend fun addCompletedSchedule(id: Long)

    suspend fun removeCompletedSchedule(id: Long)
}
