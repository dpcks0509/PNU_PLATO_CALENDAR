package pusan.university.plato_calendar.presentation.cafeteria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.presentation.cafeteria.component.CafeteriaSelector
import pusan.university.plato_calendar.presentation.cafeteria.component.DateSelector
import pusan.university.plato_calendar.presentation.cafeteria.component.MealCard
import pusan.university.plato_calendar.presentation.cafeteria.component.Notice
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.common.component.TopBar
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme

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

        DateSelector(state = state, onEvent = onEvent)

        CafeteriaSelector(
            selectedCafeteria = state.selectedCafeteria,
            onCafeteriaSelected = { onEvent(CafeteriaEvent.SelectCafeteria(it)) },
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val weeklyPlan = state.getWeeklyPlanByCafeteria(state.selectedCafeteria)
            if (weeklyPlan.notice.isNotEmpty()) {
                Notice(text = weeklyPlan.notice)
            }

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

@Preview(showBackground = true)
@Composable
fun CafeteriaScreenPreview() {
    PlatoCalendarTheme {
        CafeteriaScreen(modifier = Modifier.fillMaxSize())
    }
}
