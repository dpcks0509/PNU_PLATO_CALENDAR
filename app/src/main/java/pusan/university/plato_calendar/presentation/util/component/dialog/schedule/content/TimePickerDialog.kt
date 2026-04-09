package pusan.university.plato_calendar.presentation.util.component.dialog.schedule.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.util.extension.noRippleClickable
import pusan.university.plato_calendar.presentation.util.theme.Black
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.LightGray
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.White
import kotlin.math.abs

private enum class TimeField { HOUR, MINUTE }

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    var selectedHour by rememberSaveable { mutableIntStateOf(initialHour) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(initialMinute) }
    var editingField by rememberSaveable { mutableStateOf<TimeField?>(null) }
    var editingText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val hourRange = 0..23
    val minuteRange = 0..59

    Dialog(onDismissRequest = onDismiss) {
        val focusManager = LocalFocusManager.current

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    focusManager.clearFocus()
                },
        ) {
            Column(
                modifier =
                    Modifier
                        .background(White)
                        .padding(16.dp),
            ) {
                Text(
                    text = "시간 선택",
                    fontSize = 14.sp,
                    color = Black,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                TimePicker(
                    hour = selectedHour,
                    minute = selectedMinute,
                    onTimeChange = { hour, minute ->
                        selectedHour = hour
                        selectedMinute = minute
                    },
                    editingField = editingField,
                    editingText = editingText,
                    onEditingFieldChange = { editingField = it },
                    onEditingTextChange = { editingText = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "취소",
                            color = Black,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = {
                        val effectiveHour = if (editingField == TimeField.HOUR) {
                            editingText.text.toIntOrNull()?.coerceIn(hourRange) ?: selectedHour
                        } else selectedHour
                        val effectiveMinute = if (editingField == TimeField.MINUTE) {
                            editingText.text.toIntOrNull()?.coerceIn(minuteRange) ?: selectedMinute
                        } else selectedMinute
                        onConfirm(effectiveHour, effectiveMinute)
                    }) {
                        Text(
                            text = "확인",
                            color = PrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimePicker(
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    editingField: TimeField?,
    editingText: TextFieldValue,
    onEditingFieldChange: (TimeField?) -> Unit,
    onEditingTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemHeight = 56.dp
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val visibleItemsCount = 3

    val hourRange = 0..23
    val minuteRange = 0..59

    val hourListState = rememberLazyListState(initialFirstVisibleItemIndex = hour)
    val minuteListState = rememberLazyListState(initialFirstVisibleItemIndex = minute)

    val hourSnapBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
    val minuteSnapBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)

    val currentHour by remember {
        derivedStateOf {
            val firstVisibleItem = hourListState.firstVisibleItemIndex
            val offset = hourListState.firstVisibleItemScrollOffset
            if (offset > itemHeightPx / 2) {
                (firstVisibleItem + 1).coerceIn(hourRange)
            } else {
                firstVisibleItem.coerceIn(hourRange)
            }
        }
    }

    val currentMinute by remember {
        derivedStateOf {
            val firstVisibleItem = minuteListState.firstVisibleItemIndex
            val offset = minuteListState.firstVisibleItemScrollOffset
            if (offset > itemHeightPx / 2) {
                (firstVisibleItem + 1).coerceIn(minuteRange)
            } else {
                firstVisibleItem.coerceIn(minuteRange)
            }
        }
    }

    LaunchedEffect(hourListState, minuteListState) {
        snapshotFlow {
            currentHour to currentMinute
        }.distinctUntilChanged()
            .collect { (h, m) ->
                onTimeChange(h, m)
            }
    }

    Box(modifier = modifier) {
        Column {
            HorizontalDivider(
                color = LightGray,
                thickness = 1.dp,
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight * visibleItemsCount),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PickerColumn(
                    items = hourRange.toList(),
                    listState = hourListState,
                    flingBehavior = hourSnapBehavior,
                    itemHeight = itemHeight,
                    visibleItemsCount = visibleItemsCount,
                    currentIndex = currentHour,
                    modifier = Modifier.weight(1f),
                    formatItem = { it.toString().padStart(2, '0') },
                    isEditing = editingField == TimeField.HOUR,
                    editingText = if (editingField == TimeField.HOUR) editingText else TextFieldValue(""),
                    onScrollToItem = { value -> coroutineScope.launch { hourListState.animateScrollToItem(value) } },
                    onStartEditing = {
                        val text = currentHour.toString().padStart(2, '0')
                        onEditingFieldChange(TimeField.HOUR)
                        onEditingTextChange(TextFieldValue(text = text, selection = TextRange(0, text.length)))
                    },
                    onEditingTextChange = {
                        if (editingField == TimeField.HOUR) {
                            onEditingTextChange(it)
                        }
                    },
                    onEditingComplete = { text ->
                        val value = text.toIntOrNull()?.coerceIn(hourRange)
                        if (value != null) {
                            coroutineScope.launch { hourListState.scrollToItem(value) }
                        }
                        onEditingFieldChange(null)
                        onEditingTextChange(TextFieldValue(""))
                    },
                )

                Text(
                    text = ":",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                PickerColumn(
                    items = minuteRange.toList(),
                    listState = minuteListState,
                    flingBehavior = minuteSnapBehavior,
                    itemHeight = itemHeight,
                    visibleItemsCount = visibleItemsCount,
                    currentIndex = currentMinute,
                    modifier = Modifier.weight(1f),
                    formatItem = { it.toString().padStart(2, '0') },
                    isEditing = editingField == TimeField.MINUTE,
                    editingText = if (editingField == TimeField.MINUTE) editingText else TextFieldValue(""),
                    onScrollToItem = { value -> coroutineScope.launch { minuteListState.animateScrollToItem(value) } },
                    onStartEditing = {
                        val text = currentMinute.toString().padStart(2, '0')
                        onEditingFieldChange(TimeField.MINUTE)
                        onEditingTextChange(TextFieldValue(text = text, selection = TextRange(0, text.length)))
                    },
                    onEditingTextChange = {
                        if (editingField == TimeField.MINUTE) {
                            onEditingTextChange(it)
                        }
                    },
                    onEditingComplete = { text ->
                        val value = text.toIntOrNull()?.coerceIn(minuteRange)
                        if (value != null) {
                            coroutineScope.launch { minuteListState.scrollToItem(value) }
                        }
                        onEditingFieldChange(null)
                        onEditingTextChange(TextFieldValue(""))
                    },
                )
            }

            HorizontalDivider(
                color = LightGray,
                thickness = 1.dp,
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
        ) {
            HorizontalDivider(
                color = LightGray,
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            HorizontalDivider(
                color = LightGray,
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PickerColumn(
    items: List<Int>,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    itemHeight: Dp,
    visibleItemsCount: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    formatItem: (Int) -> String,
    isEditing: Boolean,
    editingText: TextFieldValue,
    onStartEditing: () -> Unit,
    onScrollToItem: (Int) -> Unit,
    onEditingTextChange: (TextFieldValue) -> Unit,
    onEditingComplete: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    var prevImeBottom by rememberSaveable { mutableIntStateOf(imeBottom) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(imeBottom) {
        if (isEditing && prevImeBottom > 0 && imeBottom < prevImeBottom) {
            onEditingComplete(editingText.text)
        }
        prevImeBottom = imeBottom
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItemsCount),
        contentAlignment = Alignment.Center,
    ) {
        if (isEditing) {
            BasicTextField(
                value = editingText,
                onValueChange = { newValue ->
                    if (newValue.text.length <= 2 && newValue.text.all { it.isDigit() }) {
                        onEditingTextChange(newValue)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEditingComplete(editingText.text) },
                ),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    textAlign = TextAlign.Center,
                ),
                singleLine = true,
                modifier = Modifier
                    .width(56.dp)
                    .focusRequester(focusRequester),
            )
        } else {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * visibleItemsCount),
            ) {
                items(count = items.size + visibleItemsCount - 1) { index ->
                    val itemIndex = index - (visibleItemsCount / 2)
                    val item = items.getOrNull(itemIndex)

                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (item != null) {
                            val isSelected = itemIndex == currentIndex
                            val alpha =
                                when {
                                    isSelected -> 1f
                                    abs(itemIndex - currentIndex) == 1 -> 0.5f
                                    else -> 0.3f
                                }

                            Text(
                                text = formatItem(item),
                                fontSize = if (isSelected) 24.sp else 20.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Black else Gray,
                                textAlign = TextAlign.Center,
                                modifier =
                                    Modifier
                                        .alpha(alpha)
                                        .offset(y = if (isSelected) 0.dp else 0.dp)
                                        .clickable {
                                            if (isSelected) onStartEditing()
                                            else onScrollToItem(item)
                                        },
                            )
                        }
                    }
                }
            }
        }
    }
}
