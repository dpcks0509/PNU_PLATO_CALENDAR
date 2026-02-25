package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): ApiResult<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): ApiResult<List<PersonalSchedule>>

    suspend fun makeCustomSchedule(newSchedule: NewSchedule): ApiResult<Long>

    suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): ApiResult<Unit>

    suspend fun deleteCustomSchedule(id: Long): ApiResult<Unit>
}
