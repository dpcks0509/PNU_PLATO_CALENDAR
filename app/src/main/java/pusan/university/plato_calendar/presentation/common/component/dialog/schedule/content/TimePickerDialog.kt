package pusan.university.plato_calendar.presentation.common.component.dialog.schedule.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.distinctUntilChanged
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.LightGray
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.White
import kotlin.math.abs

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    var selectedHour by rememberSaveable { mutableIntStateOf(initialHour) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
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

                    TextButton(onClick = { onConfirm(selectedHour, selectedMinute) }) {
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

private @OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimePicker(
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(itemHeight * visibleItemsCount),
        ) {
            items(items.size + visibleItemsCount - 1) { index ->
                val itemIndex = index - (visibleItemsCount / 2)
                val item = items.getOrNull(itemIndex)

                Box(
                    modifier =
                        Modifier
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
                            color = if (isSelected) Black else Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier =
                                Modifier
                                    .alpha(alpha)
                                    .offset(y = if (isSelected) 0.dp else 0.dp),
                        )
                    }
                }
            }
        }
    }
}
