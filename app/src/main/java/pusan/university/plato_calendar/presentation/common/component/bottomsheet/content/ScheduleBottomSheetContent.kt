package pusan.university.plato_calendar.presentation.common.component.bottomsheet.content

import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel

sealed interface ScheduleBottomSheetContent {
    data class AcademicScheduleContent(
        val schedule: AcademicScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data class CourseScheduleContent(
        val schedule: CourseScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data class CustomScheduleContent(
        val schedule: CustomScheduleUiModel,
    ) : ScheduleBottomSheetContent

    data object NewScheduleContent : ScheduleBottomSheetContent
}
