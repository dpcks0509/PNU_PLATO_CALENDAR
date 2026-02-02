package pusan.university.plato_calendar.presentation.cafeteria

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pusan.university.plato_calendar.R
import pusan.university.plato_calendar.domain.entity.Cafeteria
import pusan.university.plato_calendar.domain.entity.CafeteriaPlan
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.component.TopBar
import pusan.university.plato_calendar.presentation.common.theme.Black
import pusan.university.plato_calendar.presentation.common.theme.Gray
import pusan.university.plato_calendar.presentation.common.theme.LightGray
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.common.theme.VeryLightGray
import pusan.university.plato_calendar.presentation.common.theme.White

@Composable
fun CafeteriaScreen(
    modifier: Modifier,
    viewModel: CafeteriaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { }
    }

    CafeteriaContent(
        state = state,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@Composable
fun CafeteriaContent(
    state: CafeteriaState,
    onEvent: (CafeteriaEvent) -> Unit,
    modifier: Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
    ) {
        TopBar(title = "학식")

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 식당 선택 및 날짜 선택
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val weekStartDate = state.getWeekStartDate()
                val weekEndDate = state.getWeekEndDate()
                val canGoPrevious = weekStartDate?.let { !state.selectedDate.isEqual(it) } ?: false
                val canGoNext = weekEndDate?.let { !state.selectedDate.isEqual(it) } ?: false

                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = { if (canGoPrevious) onEvent(CafeteriaEvent.PreviousDay) },
                                enabled = canGoPrevious,
                            ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = if (canGoPrevious) Gray else LightGray,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val dayOfWeek =
                        when (state.selectedDate.dayOfWeek.value) {
                            1 -> "월"
                            2 -> "화"
                            3 -> "수"
                            4 -> "목"
                            5 -> "금"
                            6 -> "토"
                            7 -> "일"
                            else -> ""
                        }

                    Text(
                        text =
                            "${state.selectedDate.monthValue.toString().padStart(2, '0')}월 ${
                                state.selectedDate.dayOfMonth.toString().padStart(2, '0')
                            }일 ($dayOfWeek)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = { if (canGoNext) onEvent(CafeteriaEvent.NextDay) },
                                enabled = canGoNext,
                            ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (canGoNext) Gray else LightGray,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            CafeteriaSelector(
                selectedCafeteria = state.selectedCafeteria,
                onCafeteriaSelected = { onEvent(CafeteriaEvent.SelectCafeteria(it)) },
            )

            // 공지사항 표시
            val weeklyPlan = state.getWeeklyPlanByCafeteria(state.selectedCafeteria)
            if (weeklyPlan.notice.isNotEmpty()) {
                NoticeBox(text = weeklyPlan.notice)
            }

            // 메뉴 표시 - 조식, 중식, 석식 순서대로
            val dailyPlan = state.getCurrentDailyPlan()
            if (dailyPlan != null) {
                MealType.entries.forEach { mealType ->
                    val plansForMealType = dailyPlan.getDailyPlansByMealType(mealType)
                    if (plansForMealType.isNotEmpty()) {
                        MealCard(
                            mealType = mealType,
                            plans = plansForMealType,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CafeteriaSelector(
    selectedCafeteria: Cafeteria,
    onCafeteriaSelected: (Cafeteria) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { expanded = true },
                    ).padding(vertical = 4.dp),
        ) {
            Text(
                text = selectedCafeteria.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(20.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Cafeteria.entries.forEach { cafeteria ->
                DropdownMenuItem(
                    text = { Text(cafeteria.title) },
                    onClick = {
                        onCafeteriaSelected(cafeteria)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun MealCard(
    mealType: MealType,
    plans: List<CafeteriaPlan>,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(20.dp),
        ) {
            // 공지사항 (운영 안함 or 기타 알림) - 첫 번째 plan만 확인
            val firstPlan = plans.first()

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = mealType.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                )

                if (firstPlan.operatingTime != null) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "운영시간: ${firstPlan.operatingTime}",
                        fontSize = 14.sp,
                        color = Gray,
                    )
                }
            }

            if (!firstPlan.isOperating) {
                Box(
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VeryLightGray)
                            .padding(16.dp),
                ) {
                    Text(
                        text = firstPlan.notOperatingReason ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray,
                    )
                }
            } else {
                Column(
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE6F7FF)) // TODO
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    plans.forEach { plan ->
                        Column {
                            // 가격 및 코스 제목
                            plan.courseTitle?.let { title ->
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryColor,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 메뉴
                            plan.menus?.let { menus ->
                                Text(
                                    text = menus,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Gray,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF9E6)) // TODO
                .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(12.dp)) // TODO
                .padding(16.dp),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF6D4C00), // TODO
            lineHeight = 20.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CafeteriaScreenPreview() {
    PlatoCalendarTheme {
        CafeteriaScreen(modifier = Modifier.fillMaxSize())
    }
}
