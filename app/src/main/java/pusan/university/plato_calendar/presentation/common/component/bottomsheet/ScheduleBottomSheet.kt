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
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
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
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
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
                        onDismissRequest = onDismissRequest,
                    )
                }

                is CourseScheduleContent -> {
                    CourseScheduleContent(
                        schedule = content.schedule,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        onDismissRequest = onDismissRequest,
                    )
                }

                is CustomScheduleContent -> {
                    CustomScheduleContent(
                        schedule = content.schedule,
                        editSchedule = editSchedule,
                        toggleScheduleCompletion = toggleScheduleCompletion,
                        onDeleteRequest = {
                            scope.launch {
                                DialogEventBus.show(
                                    DialogContent.DeleteSchedule(
                                        scheduleId = content.schedule.id,
                                        onConfirm = { deleteSchedule(content.schedule.id) },
                                    ),
                                )
                            }
                        },
                        onDismissRequest = onDismissRequest,
                    )
                }

                is NewScheduleContent -> {
                    NewScheduleContent(
                        selectedDate = selectedDate,
                        makeSchedule = makeSchedule,
                        onDismissRequest = onDismissRequest,
                    )
                }

                null -> Unit
            }
        }
    }
}
