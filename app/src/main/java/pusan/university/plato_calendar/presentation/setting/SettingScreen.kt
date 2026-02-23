package pusan.university.plato_calendar.presentation.setting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.common.component.TopBar
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent
import pusan.university.plato_calendar.presentation.common.eventbus.DialogEventBus
import pusan.university.plato_calendar.presentation.common.theme.MediumGray
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.setting.component.Account
import pusan.university.plato_calendar.presentation.setting.component.NotificationToggleItem
import pusan.university.plato_calendar.presentation.setting.component.ReminderDropdownItem
import pusan.university.plato_calendar.presentation.setting.component.SettingItem
import pusan.university.plato_calendar.presentation.setting.component.SettingSection
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.NavigateToWebView
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.UpdateFirstReminderTime
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.UpdateNotificationPermission
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.UpdateNotificationsEnabled
import pusan.university.plato_calendar.presentation.setting.intent.SettingEvent.UpdateSecondReminderTime
import pusan.university.plato_calendar.presentation.setting.intent.SettingSideEffect
import pusan.university.plato_calendar.presentation.setting.intent.SettingState
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.ACCOUNT
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.NOTIFICATIONS
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.FIRST_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.NOTIFICATIONS_ENABLED
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.SettingContent.SECOND_REMINDER
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.USAGE_GUIDE
import pusan.university.plato_calendar.presentation.setting.model.SettingMenu.USER_SUPPORT

@Composable
fun SettingScreen(
    navigateToWebView: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        val granted = checkNotificationPermission(context)
        viewModel.setEvent(UpdateNotificationPermission(granted))
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.sideEffect.collect { sideEffect ->
                when (sideEffect) {
                    is SettingSideEffect.NavigateToWebView -> navigateToWebView(sideEffect.url)
                }
            }
        }
    }

    val handleSettingEvent: (SettingEvent) -> Unit = { event ->
        when (event) {
            is UpdateNotificationsEnabled -> {
                if (!event.enabled) {
                    viewModel.setEvent(event)
                } else {
                    if (checkNotificationPermission(context)) {
                        viewModel.setEvent(event)
                    } else {
                        coroutineScope.launch {
                            DialogEventBus.show(PlatoDialogContent.NotificationPermissionContent)
                        }
                    }
                }
            }

            else -> {
                viewModel.setEvent(event)
            }
        }
    }

    SettingContent(
        state = state,
        lazyListState = lazyListState,
        coroutineScope = coroutineScope,
        onEvent = handleSettingEvent,
        modifier = modifier,
    )
}

@Composable
fun SettingContent(
    state: SettingState,
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
    ) {
        TopBar(title = "설정")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingMenu.entries.forEach { menu ->
                item {
                    SettingSection(title = menu.title) {
                        when (menu) {
                            ACCOUNT -> {
                                Account(
                                    userInfo = state.userInfo,
                                    onClickLoginLogout = {
                                        val isLoggedIn = state.userInfo != null
                                        if (isLoggedIn) {
                                            onEvent(SettingEvent.Logout)
                                        } else {
                                            coroutineScope.launch {
                                                DialogEventBus.show(PlatoDialogContent.LoginContent)
                                            }
                                        }
                                    },
                                )
                            }

                            NOTIFICATIONS -> {
                                menu.items.forEachIndexed { index, content ->
                                    when {
                                        content == NOTIFICATIONS_ENABLED -> {
                                            NotificationToggleItem(
                                                label = content.getLabel(),
                                                checked = state.notificationsEnabled,
                                                onCheckedChange = { enabled ->
                                                    onEvent(
                                                        UpdateNotificationsEnabled(enabled),
                                                    )
                                                },
                                            )
                                        }

                                        content == FIRST_REMINDER -> {
                                            ReminderDropdownItem(
                                                label = content.getLabel(),
                                                selectedLabel = state.firstReminderTime.label,
                                                enabled = state.hasNotificationPermission,
                                                onSelect = { option ->
                                                    onEvent(
                                                        UpdateFirstReminderTime(
                                                            option,
                                                        ),
                                                    )
                                                },
                                            )
                                        }

                                        content == SECOND_REMINDER -> {
                                            ReminderDropdownItem(
                                                label = content.getLabel(),
                                                selectedLabel = state.secondReminderTime.label,
                                                onSelect = { option ->
                                                    onEvent(
                                                        UpdateSecondReminderTime(
                                                            option,
                                                        ),
                                                    )
                                                },
                                                enabled = state.hasNotificationPermission,
                                            )
                                        }

                                        index != menu.items.lastIndex -> {
                                            Spacer(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height(1.dp)
                                                        .background(MediumGray),
                                            )
                                        }
                                    }
                                }
                            }

                            USER_SUPPORT, USAGE_GUIDE -> {
                                menu.items.forEachIndexed { index, content ->
                                    SettingItem(
                                        content = content,
                                        navigateToWebView = { navigateUrl ->
                                            onEvent(
                                                NavigateToWebView(
                                                    navigateUrl,
                                                ),
                                            )
                                        },
                                    )

                                    if (index != menu.items.lastIndex) {
                                        Spacer(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(MediumGray),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun checkNotificationPermission(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    PlatoCalendarTheme {
        SettingContent(
            state = SettingState(),
            lazyListState = rememberLazyListState(),
            coroutineScope = rememberCoroutineScope(),
            onEvent = {},
        )
    }
}
