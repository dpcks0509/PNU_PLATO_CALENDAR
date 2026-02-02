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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
    ) {
        TopBar(title = "학식")
    }
}

@Preview(showBackground = true)
@Composable
fun CafeteriaScreenPreview() {
    PlatoCalendarTheme {
        CafeteriaScreen(modifier = Modifier.fillMaxSize())
    }
}
