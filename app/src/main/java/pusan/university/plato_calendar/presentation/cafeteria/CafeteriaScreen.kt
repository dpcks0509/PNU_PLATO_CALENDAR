package pusan.university.plato_calendar.presentation.cafeteria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pusan.university.plato_calendar.domain.entity.Cafeteria
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

    var expandedCafeterias by rememberSaveable {
        mutableStateOf<Cafeteria?>(
            state.pinnedCafeteria ?: Cafeteria.GEUMJEONG_STUDENT
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
    ) {
        TopBar(title = "학식")

        val cafeterias by remember(state.pinnedCafeteria) {
            derivedStateOf {
                val pinned = state.pinnedCafeteria
                if (pinned != null) {
                    listOf(pinned) + Cafeteria.entries.filter { it != pinned }
                } else {
                    Cafeteria.entries
                }
            }
        }

        cafeterias.forEach { cafeteria ->

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