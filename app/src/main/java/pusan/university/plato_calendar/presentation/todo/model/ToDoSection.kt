package pusan.university.plato_calendar.presentation.todo.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import pusan.university.plato_calendar.R

@Parcelize
enum class ToDoSection(
    val title: String,
    @DrawableRes val icon: Int,
) : Parcelable {
    WITHIN_7_DAYS("7일 이내", R.drawable.ic_upcoming),
    COMPLETED("완료", R.drawable.ic_complete),
    COURSE("수업 일정", R.drawable.ic_book),
    CUSTOM("개인 일정", R.drawable.ic_person),
    ACADEMIC("학사 일정", R.drawable.ic_academic),
}
