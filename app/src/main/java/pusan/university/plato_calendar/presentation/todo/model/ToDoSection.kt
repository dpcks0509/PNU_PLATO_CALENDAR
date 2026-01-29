package pusan.university.plato_calendar.presentation.todo.model

import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.presentation.common.icon.IconType

enum class ToDoSection(
    val title: String,
    val icon: IconType,
) {
    WITHIN_7_DAYS("7일 이내", IconType.Resource(R.drawable.ic_upcoming)),
    COMPLETED("완료", IconType.Resource(R.drawable.ic_complete)),
    COURSE("수업 일정", IconType.Resource(R.drawable.ic_book)),
    CUSTOM("개인 일정", IconType.Resource(R.drawable.ic_person)),
    ACADEMIC("학사 일정", IconType.Resource(R.drawable.ic_academic)),
}
