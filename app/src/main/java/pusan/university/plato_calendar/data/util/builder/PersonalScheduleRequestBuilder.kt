package pusan.university.plato_calendar.data.util.builder

import pusan.university.plato_calendar.data.request.CreatePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.CreatePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleEvent
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.UpdatePersonalScheduleArgs
import pusan.university.plato_calendar.data.request.UpdatePersonalScheduleRequest
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import java.net.URLEncoder
import java.time.LocalDateTime

internal fun buildCreatePersonalScheduleRequest(
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

internal fun buildUpdatePersonalScheduleRequest(
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

internal fun buildDeletePersonalScheduleRequest(eventId: Long): List<DeletePersonalScheduleRequest> =
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
