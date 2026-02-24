package pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.AcademicScheduleBottomSheet
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.CourseScheduleBottomSheet
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.CustomScheduleBottomSheet
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.NewScheduleBottomSheet
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.util.component.bottomsheet.schedule.content.ScheduleBottomSheetContent.NewScheduleContent
import pusan.university.plato_calendar.presentation.util.component.dialog.schedule.content.ScheduleDialogContent
import pusan.university.plato_calendar.presentation.util.theme.White
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    content: ScheduleBottomSheetContent?,
    selectedDate: LocalDate,
    sheetState: SheetState,
    makeSchedule: (NewSchedule) -> Unit,
    editSchedule: (CustomSchedule) -> Unit,
    deleteSchedule: (Long) -> Unit,
    toggleScheduleCompletion: (Long, Boolean) -> Unit,
    onShowDialog: (ScheduleDialogContent) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    content?.let { content ->
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            dragHandle = null,
            modifier = modifier,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(White)
                        .clip(RoundedCornerShape(8.dp))
                        .verticalScroll(scrollState),
            ) {
                when (content) {
                    is AcademicScheduleContent -> {
                        AcademicScheduleBottomSheet(
                            schedule = content.schedule,
                            onDismiss = onDismiss,
                        )
                    }

                    is CourseScheduleContent -> {
                        CourseScheduleBottomSheet(
                            schedule = content.schedule,
                            toggleScheduleCompletion = toggleScheduleCompletion,
                            onDismiss = onDismiss,
                        )
                    }

                    is CustomScheduleContent -> {
                        CustomScheduleBottomSheet(
                            schedule = content.schedule,
                            editSchedule = editSchedule,
                            toggleScheduleCompletion = toggleScheduleCompletion,
                            deleteSchedule = {
                                onShowDialog(
                                    ScheduleDialogContent.DeleteScheduleContent(
                                        scheduleId = content.schedule.id,
                                        onConfirm = { deleteSchedule(content.schedule.id) },
                                    ),
                                )
                            },
                            onShowDialog = onShowDialog,
                            onDismiss = onDismiss,
                        )
                    }

                    is NewScheduleContent -> {
                        NewScheduleBottomSheet(
                            selectedDate = selectedDate,
                            makeSchedule = makeSchedule,
                            onShowDialog = onShowDialog,
                            onDismiss = onDismiss,
                        )
                    }
                }
            }
        }
    }
}
