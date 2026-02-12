package pusan.university.plato_calendar.presentation.common.component.bottomsheet

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.NewScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.ScheduleBottomSheetContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.content.ScheduleBottomSheetContent.NewScheduleContent
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DialogContent
import pusan.university.plato_calendar.presentation.common.eventbus.DialogEventBus
import pusan.university.plato_calendar.presentation.common.theme.White
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
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

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
                    AcademicScheduleContent(
                        schedule = content.schedule,
                        onDismiss = onDismiss,
                    )
                }

                is CourseScheduleContent -> {
                    CourseScheduleContent(
                        schedule = content.schedule,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        onDismiss = onDismiss,
                    )
                }

                is CustomScheduleContent -> {
                    CustomScheduleContent(
                        schedule = content.schedule,
                        editSchedule = editSchedule,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        deleteSchedule = {
                            coroutineScope.launch {
                                DialogEventBus.show(
                                    DialogContent.DeleteScheduleContent(
                                        scheduleId = content.schedule.id,
                                        onConfirm = { deleteSchedule(content.schedule.id) },
                                    ),
                                )
                            }
                        },
                        onDismiss = onDismiss,
                    )
                }

                is NewScheduleContent -> {
                    NewScheduleContent(
                        selectedDate = selectedDate,
                        makeSchedule = makeSchedule,
                        onDismiss = onDismiss,
                    )
                }

                null -> Unit
            }
        }
    }
}
