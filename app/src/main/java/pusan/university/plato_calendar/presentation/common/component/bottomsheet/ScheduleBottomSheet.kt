package pusan.university.plato_calendar.presentation.common.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pusan.university.plato_calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
import pusan.university.plato_calendar.presentation.common.theme.Red
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "일정 삭제") },
            text = { Text(text = "일정을 삭제하시겠습니까?\n삭제된 일정은 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (content is CustomScheduleContent) {
                            showDeleteDialog = false
                            deleteSchedule(content.schedule.id)
                        }
                    },
                ) { Text(text = "삭제", color = Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(text = "취소") }
            },
        )
    }

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
                        onDeleteRequest = { showDeleteDialog = true },
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

                null -> {
                    Unit
                }
            }
        }
    }
}
