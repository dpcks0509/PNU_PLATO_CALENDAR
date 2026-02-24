package pusan.university.plato_calendar.presentation.util.serializer

import org.json.JSONArray
import org.json.JSONObject
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import java.time.LocalDateTime

object PersonalScheduleSerializer {
    fun serializePersonalSchedules(personalSchedules: List<PersonalScheduleUiModel>): String {
        val jsonArray = JSONArray()

        personalSchedules.forEach { personalSchedule ->
            val jsonObject = JSONObject()

            when (personalSchedule) {
                is PersonalScheduleUiModel.CourseScheduleUiModel -> {
                    jsonObject.put("type", "course")
                    jsonObject.put("id", personalSchedule.id)
                    jsonObject.put("title", personalSchedule.title)
                    jsonObject.put("description", personalSchedule.description ?: "")
                    jsonObject.put("startAt", personalSchedule.startAt.toString())
                    jsonObject.put("endAt", personalSchedule.endAt.toString())
                    jsonObject.put("isCompleted", personalSchedule.isCompleted)
                    jsonObject.put("courseName", personalSchedule.courseName)
                }

                is PersonalScheduleUiModel.CustomScheduleUiModel -> {
                    jsonObject.put("type", "custom")
                    jsonObject.put("id", personalSchedule.id)
                    jsonObject.put("title", personalSchedule.title)
                    jsonObject.put("description", personalSchedule.description ?: "")
                    jsonObject.put("startAt", personalSchedule.startAt.toString())
                    jsonObject.put("endAt", personalSchedule.endAt.toString())
                    jsonObject.put("isCompleted", personalSchedule.isCompleted)
                }
            }

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    fun deserializePersonalSchedules(json: String): List<PersonalScheduleUiModel> {
        if (json.isEmpty()) return emptyList()

        val jsonArray = JSONArray(json)
        val personalSchedules = mutableListOf<PersonalScheduleUiModel>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("type")

            val personalSchedule =
                when (type) {
                    "course" -> {
                        PersonalScheduleUiModel.CourseScheduleUiModel(
                            id = jsonObject.getLong("id"),
                            title = jsonObject.getString("title"),
                            description = jsonObject.getString("description")
                                .takeIf { it.isNotEmpty() },
                            startAt = LocalDateTime.parse(jsonObject.getString("startAt")),
                            endAt = LocalDateTime.parse(jsonObject.getString("endAt")),
                            isCompleted = jsonObject.getBoolean("isCompleted"),
                            courseName = jsonObject.getString("courseName"),
                        )
                    }

                    "custom" -> {
                        PersonalScheduleUiModel.CustomScheduleUiModel(
                            id = jsonObject.getLong("id"),
                            title = jsonObject.getString("title"),
                            description = jsonObject.getString("description")
                                .takeIf { it.isNotEmpty() },
                            startAt = LocalDateTime.parse(jsonObject.getString("startAt")),
                            endAt = LocalDateTime.parse(jsonObject.getString("endAt")),
                            isCompleted = jsonObject.getBoolean("isCompleted"),
                        )
                    }

                    else -> null
                }

            personalSchedule?.let { personalSchedules.add(it) }
        }

        return personalSchedules
    }
}
