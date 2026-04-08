package pusan.university.plato_calendar.presentation.cafeteria

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.cafeteria.component.CafeteriaMealInfoPage
import pusan.university.plato_calendar.presentation.cafeteria.component.DateSelector
import pusan.university.plato_calendar.presentation.cafeteria.component.DormitoryMealInfoPage
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.Refresh
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaEvent.SelectTab
import pusan.university.plato_calendar.presentation.cafeteria.intent.CafeteriaState
import pusan.university.plato_calendar.presentation.cafeteria.model.CafeteriaTab
import pusan.university.plato_calendar.presentation.util.component.PullToRefreshContainer
import pusan.university.plato_calendar.presentation.util.component.TopBar
import pusan.university.plato_calendar.presentation.util.theme.Gray
import pusan.university.plato_calendar.presentation.util.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.util.theme.PrimaryColor
import pusan.university.plato_calendar.presentation.util.theme.White

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
    val pagerState = rememberPagerState(initialPage = state.selectedTab.ordinal) { CafeteriaTab.entries.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onEvent(SelectTab(CafeteriaTab.entries[pagerState.currentPage]))
    }

    LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab.ordinal) {
            pagerState.scrollToPage(state.selectedTab.ordinal)
        }
    }

    PullToRefreshContainer(
        modifier = modifier,
        onRefresh = { onEvent(Refresh) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(title = "식당")

            TabRow(
                selectedTabIndex = pagerState.currentPage, containerColor = White, modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = PrimaryColor
                    )
                }) {
                CafeteriaTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        selectedContentColor = PrimaryColor,
                        unselectedContentColor = Gray,
                    )
                }
            }

            DateSelector(state = state, onEvent = onEvent)

            HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
                when (page) {
                    0 -> CafeteriaMealInfoPage(state = state, onEvent = onEvent)
                    1 -> DormitoryMealInfoPage(state = state, onEvent = onEvent)
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
