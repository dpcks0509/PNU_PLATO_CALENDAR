package pusan.university.plato_calendar.data.remote.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.data.local.database.CompletedScheduleDataStore
import pusan.university.plato_calendar.data.remote.service.AcademicScheduleService
import pusan.university.plato_calendar.data.remote.service.PersonalScheduleService
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.builder.buildCreatePersonalScheduleRequest
import pusan.university.plato_calendar.data.util.builder.buildDeletePersonalScheduleRequest
import pusan.university.plato_calendar.data.util.builder.buildUpdatePersonalScheduleRequest
import pusan.university.plato_calendar.data.util.handleApiResponse
import pusan.university.plato_calendar.data.util.parser.parseHtmlToAcademicSchedules
import pusan.university.plato_calendar.data.util.parser.parseIcsToPersonalSchedules
import pusan.university.plato_calendar.data.util.toApiResult
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import javax.inject.Inject

class RemoteScheduleRepository
    @Inject
    constructor(
        private val personalScheduleService: PersonalScheduleService,
        private val academicScheduleService: AcademicScheduleService,
        private val completedScheduleDataStore: CompletedScheduleDataStore,
        private val loginManager: LoginManager,
    ) : ScheduleRepository {
        override suspend fun getAcademicSchedules(): ApiResult<List<AcademicSchedule>> {
            val response = handleApiResponse { academicScheduleService.readAcademicSchedules() }

            return response.toApiResult(GET_SCHEDULES_FAILED_ERROR) { body ->
                val responseBody = body?.string()
                if (responseBody.isNullOrBlank()) {
                    ApiResult.Error(Exception(GET_SCHEDULES_FAILED_ERROR))
                } else {
                    ApiResult.Success(responseBody.parseHtmlToAcademicSchedules())
                }
            }
        }

        override suspend fun getPersonalSchedules(sessKey: String): ApiResult<List<PersonalSchedule>> =
            coroutineScope {
                val currentMonthDeferred = async { getCurrentMonthPersonalSchedules(sessKey) }
                val yearDeferred = async { getYearPersonalSchedules(sessKey) }

                val currentMonthResult = currentMonthDeferred.await()
                val yearResult = yearDeferred.await()

                if (currentMonthResult !is ApiResult.Success) return@coroutineScope currentMonthResult
                if (yearResult !is ApiResult.Success) return@coroutineScope yearResult

                val currentMonthSchedules = currentMonthResult.data
                val yearSchedules = yearResult.data

                val completedIds =
                    completedScheduleDataStore.completedScheduleIds
                        .catch { emit(emptySet()) }
                        .first()

                val mappedSchedules =
                    (currentMonthSchedules + yearSchedules)
                        .distinctBy { it.id }
                        .map { schedule ->
                            when (schedule) {
                                is CourseSchedule -> {
                                    schedule.copy(
                                        isCompleted = completedIds.contains(schedule.id),
                                    )
                                }

                                is CustomSchedule -> {
                                    schedule.copy(
                                        isCompleted = completedIds.contains(schedule.id),
                                    )
                                }
                            }
                        }
                ApiResult.Success(mappedSchedules)
            }

        override suspend fun makeCustomSchedule(newSchedule: NewSchedule): ApiResult<Long> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) {
                return ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
            }

            val sessKey = loginStatus.loginSession.sessKey
            val request =
                buildCreatePersonalScheduleRequest(
                    userId = loginStatus.loginSession.userId,
                    sessKey = sessKey,
                    newSchedule = newSchedule,
                )

            val response =
                handleApiResponse {
                    personalScheduleService.createCustomSchedule(sessKey = sessKey, request = request)
                }

            return response.toApiResult(CREATE_SCHEDULE_FAILED_ERROR) { body ->
                val responseBody = body?.firstOrNull()
                if (responseBody == null || responseBody.error) {
                    ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                } else {
                    val id = responseBody.data?.event?.id
                    if (id != null) {
                        ApiResult.Success(id)
                    } else {
                        ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                    }
                }
            }
        }

        override suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): ApiResult<Unit> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) {
                return ApiResult.Error(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
            }

            val sessKey = loginStatus.loginSession.sessKey
            val request =
                buildUpdatePersonalScheduleRequest(
                    id = personalSchedule.id,
                    userId = loginStatus.loginSession.userId,
                    sessKey = sessKey,
                    name = personalSchedule.title,
                    startDateTime = personalSchedule.startAt,
                    endDateTime = personalSchedule.endAt,
                    description = personalSchedule.description.orEmpty(),
                )

            val response =
                handleApiResponse {
                    personalScheduleService.updatePersonalSchedule(
                        sessKey = sessKey,
                        request = request,
                    )
                }

            return response.toApiResult(UPDATE_SCHEDULE_FAILED_ERROR) { body ->
                val responseBody = body?.firstOrNull()
                if (responseBody == null || responseBody.error) {
                    ApiResult.Error(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
                } else {
                    ApiResult.Success(Unit)
                }
            }
        }

        override suspend fun deleteCustomSchedule(id: Long): ApiResult<Unit> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) {
                return ApiResult.Error(Exception(DELETE_SCHEDULE_FAILED_ERROR))
            }

            val sessKey = loginStatus.loginSession.sessKey
            val request = buildDeletePersonalScheduleRequest(eventId = id)

            val response =
                handleApiResponse {
                    personalScheduleService.deleteCustomSchedule(
                        sessKey = sessKey,
                        request = request,
                    )
                }

            return response.toApiResult(DELETE_SCHEDULE_FAILED_ERROR) { body ->
                val responseBody = body?.firstOrNull()
                if (responseBody == null || responseBody.error) {
                    ApiResult.Error(Exception(DELETE_SCHEDULE_FAILED_ERROR))
                } else {
                    ApiResult.Success(Unit)
                }
            }
        }

        override suspend fun markScheduleAsCompleted(id: Long) {
            completedScheduleDataStore.addCompletedSchedule(id)
        }

        override suspend fun markScheduleAsUncompleted(id: Long) {
            completedScheduleDataStore.removeCompletedSchedule(id)
        }

        private suspend fun getCurrentMonthPersonalSchedules(sessKey: String): ApiResult<List<PersonalSchedule>> {
            val response =
                handleApiResponse {
                    personalScheduleService.readCurrentMonthPersonalSchedules(sessKey = sessKey)
                }
            return response.toApiResult(GET_SCHEDULES_FAILED_ERROR) { body ->
                val bodyString = body?.string()
                if (bodyString.isNullOrBlank()) {
                    ApiResult.Success(emptyList())
                } else {
                    ApiResult.Success(bodyString.parseIcsToPersonalSchedules())
                }
            }
        }

        private suspend fun getYearPersonalSchedules(sessKey: String): ApiResult<List<PersonalSchedule>> {
            val response =
                handleApiResponse {
                    personalScheduleService.readYearPersonalSchedules(sessKey = sessKey)
                }

            return response.toApiResult(GET_SCHEDULES_FAILED_ERROR) { body ->
                val bodyString = body?.string()
                if (bodyString.isNullOrBlank()) {
                    ApiResult.Success(emptyList())
                } else {
                    ApiResult.Success(bodyString.parseIcsToPersonalSchedules())
                }
            }
        }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 불러오는데 실패했습니다."
            private const val CREATE_SCHEDULE_FAILED_ERROR = "일정 등록에 실패했습니다."
            private const val UPDATE_SCHEDULE_FAILED_ERROR = "일정 수정에 실패했습니다."
            private const val DELETE_SCHEDULE_FAILED_ERROR = "일정 삭제에 실패했습니다."
        }
    }
