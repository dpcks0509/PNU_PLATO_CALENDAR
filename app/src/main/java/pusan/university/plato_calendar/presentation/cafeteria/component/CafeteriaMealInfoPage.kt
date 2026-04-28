package pusan.university.plato_calendar.presentation.cafeteria.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pusan.university.plato_calendar.domain.entity.MealType
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectCafeteria
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState

@Composable
fun CafeteriaMealInfoPage(
    state: CafeteriaState,
    onEvent: (CafeteriaEvent) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
    ) {
        CafeteriaSelector(
            selectedCafeteria = state.selectedCafeteria,
            onCafeteriaSelected = { onEvent(SelectCafeteria(it)) },
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val notice = state.getWeeklyPlanByCafeteria(state.selectedCafeteria).notice
            if (notice.isNotEmpty()) {
                Notice(text = notice)
            }

            val dailyPlan = state.getCurrentDailyPlan()
            dailyPlan?.let {
                MealType.entries.forEach { mealType ->
                    dailyPlan.getMealInfo(mealType)?.let { mealInfo ->
                        MealCard(mealInfo = mealInfo, cafeteriaName = state.selectedCafeteria.title, date = state.selectedDate)
                    }
                }
            }
        }
    }
}