package pusan.university.plato_calendar.data.remote.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import pusan.university.plato_calendar.data.local.database.CompletedScheduleDataStore
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.data.remote.service.AcademicScheduleService
import pusan.university.plato_calendar.data.remote.service.PersonalScheduleService
import pusan.university.plato_calendar.data.request.CreatePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.CreatePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleEvent
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.UpdatePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.UpdatePersonalScheduleRequest
import pusan.university.plato_calendar.data.util.ApiResponse
import pusan.university.plato_calendar.data.util.ApiResult
import pusan.university.plato_calendar.data.util.handleApiResponse
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.domain.repository.ScheduleRepository
import pusan.university.plato_calendar.presentation.util.extension.formatCourseCode
import pusan.university.plato_calendar.presentation.util.manager.LoginManager
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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
            return when (val response = handleApiResponse { academicScheduleService.readAcademicSchedules() }) {
                is ApiResponse.Success -> {
                    val responseBody = response.data?.string()
                    if (responseBody.isNullOrBlank()) {
                        ApiResult.Error(Exception(GET_SCHEDULES_FAILED_ERROR))
                    } else {
                        ApiResult.Success(responseBody.parseHtmlToAcademicSchedules())
                    }
                }
                is ApiResponse.NetworkException -> ApiResult.Error(NoNetworkConnectivityException())
                is ApiResponse.HttpError -> {
                    ApiResult.Error(Exception(GET_SCHEDULES_FAILED_ERROR))
                }
            }
        }

        override suspend fun getPersonalSchedules(sessKey: String): ApiResult<List<PersonalSchedule>> {
            return try {
                coroutineScope {
                    val (currentMonthSchedules, yearSchedules) =
                        awaitAll(
                            async { getCurrentMonthPersonalSchedules(sessKey) },
                            async { getYearPersonalSchedules(sessKey) },
                        )

                    val completedIds = completedScheduleDataStore.completedScheduleIds.first()

                    val mappedSchedules =
                        (currentMonthSchedules + yearSchedules)
                            .distinctBy { it.id }
                            .map { schedule ->
                                when (schedule) {
                                    is CourseSchedule -> schedule.copy(isCompleted = completedIds.contains(schedule.id))
                                    is CustomSchedule -> schedule.copy(isCompleted = completedIds.contains(schedule.id))
                                }
                            }
                    ApiResult.Success(mappedSchedules)
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }

        override suspend fun makeCustomSchedule(newSchedule: NewSchedule): ApiResult<Long> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) return ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))

            val sessKey = loginStatus.loginSession.sessKey
            val request = buildCreatePersonalScheduleRequest(
                userId = loginStatus.loginSession.userId,
                sessKey = sessKey,
                newSchedule = newSchedule,
            )

            return when (
                val response = handleApiResponse {
                    personalScheduleService.createCustomSchedule(sessKey = sessKey, request = request)
                }
            ) {
                is ApiResponse.Success -> {
                    val responseBody = response.data?.firstOrNull()
                    if (responseBody == null || responseBody.error) {
                        ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                    } else {
                        val id = responseBody.data?.event?.id
                        if (id != null) ApiResult.Success(id) else ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                    }
                }
                is ApiResponse.NetworkException -> ApiResult.Error(NoNetworkConnectivityException())
                is ApiResponse.HttpError -> {
                    ApiResult.Error(Exception(CREATE_SCHEDULE_FAILED_ERROR))
                }
            }
        }

        override suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): ApiResult<Unit> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) return ApiResult.Error(Exception(UPDATE_SCHEDULE_FAILED_ERROR))

            val sessKey = loginStatus.loginSession.sessKey
            val request = buildUpdatePersonalScheduleRequest(
                id = personalSchedule.id,
                userId = loginStatus.loginSession.userId,
                sessKey = sessKey,
                name = personalSchedule.title,
                startDateTime = personalSchedule.startAt,
                endDateTime = personalSchedule.endAt,
                description = personalSchedule.description.orEmpty(),
            )

            return when (
                val response = handleApiResponse {
                    personalScheduleService.updatePersonalSchedule(sessKey = sessKey, request = request)
                }
            ) {
                is ApiResponse.Success -> {
                    val responseBody = response.data?.firstOrNull()
                    if (responseBody == null || responseBody.error) {
                        ApiResult.Error(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
                    } else {
                        ApiResult.Success(Unit)
                    }
                }
                is ApiResponse.NetworkException -> ApiResult.Error(NoNetworkConnectivityException())
                is ApiResponse.HttpError -> {
                    ApiResult.Error(Exception(UPDATE_SCHEDULE_FAILED_ERROR))
                }
            }
        }

        override suspend fun deleteCustomSchedule(id: Long): ApiResult<Unit> {
            val loginStatus = loginManager.loginStatus.value
            if (loginStatus !is LoginStatus.Login) return ApiResult.Error(Exception(DELETE_SCHEDULE_FAILED_ERROR))

            val sessKey = loginStatus.loginSession.sessKey
            val request = buildDeletePersonalScheduleRequest(eventId = id)

            return when (
                val response = handleApiResponse {
                    personalScheduleService.deleteCustomSchedule(sessKey = sessKey, request = request)
                }
            ) {
                is ApiResponse.Success -> {
                    val responseBody = response.data?.firstOrNull()
                    if (responseBody == null || responseBody.error) {
                        ApiResult.Error(Exception(DELETE_SCHEDULE_FAILED_ERROR))
                    } else {
                        ApiResult.Success(Unit)
                    }
                }
                is ApiResponse.NetworkException -> ApiResult.Error(NoNetworkConnectivityException())
                is ApiResponse.HttpError -> {
                    ApiResult.Error(Exception(DELETE_SCHEDULE_FAILED_ERROR))
                }
            }
        }

        override suspend fun markScheduleAsCompleted(id: Long) {
            completedScheduleDataStore.addCompletedSchedule(id)
        }

        override suspend fun markScheduleAsUncompleted(id: Long) {
            completedScheduleDataStore.removeCompletedSchedule(id)
        }

        private suspend fun getCurrentMonthPersonalSchedules(sessKey: String): List<PersonalSchedule> {
            return when (
                val response = handleApiResponse {
                    personalScheduleService.readCurrentMonthPersonalSchedules(sessKey = sessKey)
                }
            ) {
                is ApiResponse.Success -> {
                    val bodyString = response.data?.string()
                    if (bodyString.isNullOrBlank()) emptyList() else bodyString.parseIcsToPersonalSchedules()
                }
                else -> emptyList()
            }
        }

        private suspend fun getYearPersonalSchedules(sessKey: String): List<PersonalSchedule> {
            return when (
                val response = handleApiResponse {
                    personalScheduleService.readYearPersonalSchedules(sessKey = sessKey)
                }
            ) {
                is ApiResponse.Success -> {
                    val bodyString = response.data?.string()
                    if (bodyString.isNullOrBlank()) emptyList() else bodyString.parseIcsToPersonalSchedules()
                }
                else -> emptyList()
            }
        }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 불러오는데 실패했습니다."
            private const val CREATE_SCHEDULE_FAILED_ERROR = "일정 등록에 실패했습니다."
            private const val UPDATE_SCHEDULE_FAILED_ERROR = "일정 수정에 실패했습니다."
            private const val DELETE_SCHEDULE_FAILED_ERROR = "일정 삭제에 실패했습니다."

            private fun String.parseIcsToPersonalSchedules(): List<PersonalSchedule> {
                val unfoldedLines = mutableListOf<String>()
                lines().forEach { rawLine ->
                    if ((rawLine.startsWith(" ") || rawLine.startsWith("\t")) && unfoldedLines.isNotEmpty()) {
                        val previous = unfoldedLines.removeAt(unfoldedLines.lastIndex)
                        unfoldedLines.add(previous + rawLine.trimStart())
                    } else {
                        unfoldedLines.add(rawLine)
                    }
                }

                val personalSchedules = mutableListOf<PersonalSchedule>()
                var inEvent = false
                val currentFields = mutableMapOf<String, String>()

                unfoldedLines.forEach { line ->
                    val trimmed = line.trim()
                    when {
                        trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                            inEvent = true
                            currentFields.clear()
                        }

                        trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                            if (inEvent) {
                                personalSchedules.add(buildScheduleFromFields(currentFields.toMap()))
                            }
                            inEvent = false
                            currentFields.clear()
                        }

                        inEvent -> {
                            val colonIndex = trimmed.indexOf(':')
                            if (colonIndex > 0) {
                                val key =
                                    trimmed.substring(0, colonIndex).substringBefore(';').uppercase()
                                val value = trimmed.substring(colonIndex + 1)
                                currentFields[key] = value
                            }
                        }
                    }
                }

                return personalSchedules
            }

            private fun buildScheduleFromFields(fields: Map<String, String>): PersonalSchedule {
                val courseCode = fields["CATEGORIES"]?.split("_")[2].formatCourseCode()

                val description = fields["DESCRIPTION"]?.processIcsDescription()

                val startAt = fields["DTSTART"].orEmpty().parseUtcToKstLocalDateTime()
                val endAt = fields["DTEND"].orEmpty().parseUtcToKstLocalDateTime()

                val adjustedStartAt =
                    if (startAt == endAt && startAt.hour == 0 && startAt.minute == 0) {
                        startAt.minusMinutes(1)
                    } else {
                        startAt
                    }

                val adjustedEndAt =
                    if (endAt.hour == 0 && endAt.minute == 0) {
                        endAt.minusMinutes(1)
                    } else {
                        endAt
                    }

                return if (courseCode == null) {
                    CustomSchedule(
                        id = fields["UID"].orEmpty().split("@")[0].toLong(),
                        title = fields["SUMMARY"].orEmpty(),
                        description = description,
                        startAt = startAt,
                        endAt = endAt,
                        isCompleted = false,
                    )
                } else {
                    CourseSchedule(
                        id = fields["UID"].orEmpty().split("@")[0].toLong(),
                        title = fields["SUMMARY"].orEmpty(),
                        description = description,
                        startAt = adjustedStartAt,
                        endAt = adjustedEndAt,
                        courseCode = courseCode,
                        isCompleted = false,
                    )
                }
            }

            private fun String.processIcsDescription(): String =
                this
                    .replace(Regex("(\\\\n){2,}"), "\\\\n")
                    .replace(Regex("^(\\\\n)+"), "")
                    .replace(Regex("(\\\\n)+$"), "")
                    .replace("\\\\", "\u0001")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\;", ";")
                    .replace("\\,", ",")
                    .replace("\u0001", "\\")

            private fun String.parseUtcToKstLocalDateTime(): LocalDateTime {
                val year = substring(0, 4).toInt()
                val month = substring(4, 6).toInt()
                val day = substring(6, 8).toInt()
                val hour = substring(9, 11).toInt()
                val minute = substring(11, 13).toInt()

                val utcTime = LocalDateTime.of(year, month, day, hour, minute)
                val kstTime = utcTime.atOffset(ZoneOffset.UTC).plusHours(9).toLocalDateTime()

                return kstTime
            }

            private fun String.parseHtmlToAcademicSchedules(): List<AcademicSchedule> {
                val academicSchedules = mutableListOf<AcademicSchedule>()

                val tableRows = this.split("<tr>").drop(1)

                tableRows.forEach { row ->
                    if (row.contains("class=\"term\"") && row.contains("class=\"text\"")) {
                        val termMatch = Regex("class=\"term\"[^>]*>([^<]+)</").find(row)
                        val termText = termMatch?.groupValues?.get(1)?.trim()

                        val textMatch = Regex("class=\"text\"[^>]*>([^<]+)</").find(row)
                        val textContent = textMatch?.groupValues?.get(1)?.trim()

                        if (termText != null && textContent != null) {
                            val (startAt, endAt) = termText.parseDateRange() ?: return@forEach

                            academicSchedules.add(
                                AcademicSchedule(
                                    title = textContent,
                                    startAt = startAt,
                                    endAt = endAt,
                                ),
                            )
                        }
                    }
                }

                return academicSchedules
            }

            private fun String.parseDateRange(): Pair<LocalDate, LocalDate>? {
                val dates = this.split(" - ").map { it.trim() }
                if (dates.size != 2) return null

                val startDate = dates[0].parseKoreanDateToLocalDate() ?: return null
                val endDate = dates[1].parseKoreanDateToLocalDate() ?: return null

                return startDate to endDate
            }

            private fun String.parseKoreanDateToLocalDate(): LocalDate? {
                val parts = this.split(".")
                if (parts.size != 3) return null

                val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
                val month = parts.getOrNull(1)?.toIntOrNull() ?: return null
                val day = parts.getOrNull(2)?.toIntOrNull() ?: return null

                if (month !in 1..12) return null
                if (day !in 1..31) return null

                return if (day <= LocalDate.of(year, month, 1).lengthOfMonth()) {
                    LocalDate.of(year, month, day)
                } else {
                    null
                }
            }

            private fun buildCreatePersonalScheduleRequest(
                userId: String,
                sessKey: String,
                newSchedule: NewSchedule,
            ): List<CreatePersonalScheduleRequest> {
                val encodedName = URLEncoder.encode(newSchedule.title, "UTF-8").replace("+", "%20")
                val encodedDescription =
                    URLEncoder
                        .encode("<p>${newSchedule.description.orEmpty()}</p>", "UTF-8")
                        .replace("+", "%20")

                val formData =
                    buildString {
                        append("id=0&")
                        append("userid=$userId&")
                        append("modulename=&")
                        append("instance=0&")
                        append("visible=1&")
                        append("eventtype=user&")
                        append("sesskey=$sessKey&")
                        append("_qf__core_calendar_local_event_forms_create=1&")
                        append("mform_showmore_id_general=1&")
                        append("name=$encodedName&")
                        append("timestart%5Byear%5D=${newSchedule.startAt.year}&")
                        append("timestart%5Bmonth%5D=${newSchedule.startAt.monthValue}&")
                        append("timestart%5Bday%5D=${newSchedule.startAt.dayOfMonth}&")
                        append("timestart%5Bhour%5D=${newSchedule.startAt.hour}&")
                        append("timestart%5Bminute%5D=${newSchedule.startAt.minute}&")
                        append("description%5Btext%5D=$encodedDescription&")
                        append("description%5Bformat%5D=1&")
                        append("description%5Bitemid%5D=0&")
                        append("duration=1&")
                        append("timedurationuntil%5Byear%5D=${newSchedule.endAt.year}&")
                        append("timedurationuntil%5Bmonth%5D=${newSchedule.endAt.monthValue}&")
                        append("timedurationuntil%5Bday%5D=${newSchedule.endAt.dayOfMonth}&")
                        append("timedurationuntil%5Bhour%5D=${newSchedule.endAt.hour}&")
                        append("timedurationuntil%5Bminute%5D=${newSchedule.endAt.minute}")
                    }

                return listOf(CreatePersonalScheduleRequest(args = CreatePersonalScheduleArgs(formData = formData)))
            }

            private fun buildUpdatePersonalScheduleRequest(
                id: Long,
                userId: String,
                sessKey: String,
                name: String,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime,
                description: String,
            ): List<UpdatePersonalScheduleRequest> {
                val encodedName = URLEncoder.encode(name, "UTF-8").replace("+", "%20")
                val encodedDescription =
                    URLEncoder.encode("<p>$description</p>", "UTF-8").replace("+", "%20")

                val formData =
                    buildString {
                        append("id=$id&")
                        append("userid=$userId&")
                        append("modulename=0&")
                        append("instance=0&")
                        append("visible=1&")
                        append("eventtype=user&")
                        append("repeatid=0&")
                        append("sesskey=$sessKey&")
                        append("_qf__core_calendar_local_event_forms_update=1&")
                        append("mform_showmore_id_general=0&")
                        append("name=$encodedName&")
                        append("timestart%5Byear%5D=${startDateTime.year}&")
                        append("timestart%5Bmonth%5D=${startDateTime.monthValue}&")
                        append("timestart%5Bday%5D=${startDateTime.dayOfMonth}&")
                        append("timestart%5Bhour%5D=${startDateTime.hour}&")
                        append("timestart%5Bminute%5D=${startDateTime.minute}&")
                        append("description%5Btext%5D=$encodedDescription&")
                        append("description%5Bformat%5D=1&")
                        append("description%5Bitemid%5D=0&")
                        append("duration=1&")
                        append("timedurationuntil%5Byear%5D=${endDateTime.year}&")
                        append("timedurationuntil%5Bmonth%5D=${endDateTime.monthValue}&")
                        append("timedurationuntil%5Bday%5D=${endDateTime.dayOfMonth}&")
                        append("timedurationuntil%5Bhour%5D=${endDateTime.hour}&")
                        append("timedurationuntil%5Bminute%5D=${endDateTime.minute}")
                    }

                return listOf(UpdatePersonalScheduleRequest(args = UpdatePersonalScheduleArgs(formData = formData)))
            }

            private fun buildDeletePersonalScheduleRequest(eventId: Long): List<DeletePersonalScheduleRequest> =
                listOf(
                    DeletePersonalScheduleRequest(
                        args =
                            DeletePersonalScheduleArgs(
                                events =
                                    listOf(
                                        DeletePersonalScheduleEvent(
                                            eventId = eventId,
                                            repeat = false,
                                        ),
                                    ),
                            ),
                    ),
                )
        }
    }
