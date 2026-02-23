package pusan.university.plato_calendar.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.common.component.AnimatedToast
import pusan.university.plato_calendar.presentation.common.component.LoadingIndicator
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.PlatoDialog
import pusan.university.plato_calendar.presentation.common.component.dialog.plato.content.PlatoDialogContent
import pusan.university.plato_calendar.presentation.common.eventbus.WidgetEvent
import pusan.university.plato_calendar.presentation.common.eventbus.WidgetEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarBottomBar
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarNavHost
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_NOTIFICATION_ID
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_SCHEDULE_ID
import pusan.university.plato_calendar.presentation.common.notification.NotificationHelper
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.White
import pusan.university.plato_calendar.presentation.main.intent.MainEvent
import pusan.university.plato_calendar.presentation.main.intent.MainSideEffect
import pusan.university.plato_calendar.presentation.main.intent.MainSideEffect.NavigateToNotificationSettings
import pusan.university.plato_calendar.presentation.widget.callback.OpenNewScheduleCallback
import pusan.university.plato_calendar.presentation.widget.callback.OpenScheduleDetailCallback
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var loadingManager: LoadingManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSmartOrientation()
        if (savedInstanceState == null) {
            initializeApp()
        }

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isLoading by loadingManager.isLoading.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                lifecycleScope.launch {
                    settingsManager.setNotificationsEnabled(isGranted)
                }

                if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.setEvent(MainEvent.ShowDialog(PlatoDialogContent.NotificationPermissionContent))
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (
                        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        NavigateToNotificationSettings -> {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            startActivity(intent)
                        }

                        MainSideEffect.NavigateToCalendar -> {
                            navController.navigate(PlatoCalendarScreen.CalendarScreen) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }

            PlatoCalendarTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        bottomBar = {
                            val isWebView = currentRoute?.contains("WebView") == true
                            if (!isWebView) {
                                PlatoCalendarBottomBar(navController = navController)
                            }
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.fillMaxSize(),
                    ) { paddingValues ->
                        PlatoCalendarNavHost(
                            navController = navController,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(White)
                                    .padding(paddingValues),
                        )
                    }

                    PlatoDialog(
                        content = state.dialogContent,
                        state = state,
                        onEvent = viewModel::setEvent,
                    )

                    LoadingIndicator(isLoading = isLoading)

                    AnimatedToast()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)

        if (notificationId != -1) {
            notificationHelper.cancelNotification(notificationId)
        }

        if (scheduleId != -1L) {
            viewModel.setEvent(MainEvent.NavigateToCalendar)
            val selectedDate = intent.getStringExtra(OpenScheduleDetailCallback.EXTRA_SELECTED_DATE)
            lifecycleScope.launch {
                WidgetEventBus.sendEvent(WidgetEvent.OpenSchedule(scheduleId, selectedDate))
            }
        }

        if (intent.action == OpenNewScheduleCallback.ACTION_OPEN_NEW_SCHEDULE) {
            viewModel.setEvent(MainEvent.NavigateToCalendar)
            val selectedDate = intent.getStringExtra(OpenNewScheduleCallback.EXTRA_SELECTED_DATE)
            lifecycleScope.launch {
                WidgetEventBus.sendEvent(WidgetEvent.OpenNewSchedule(selectedDate))
            }
        }
    }

    private fun initializeApp() {
        lifecycleScope.launch {
            loginManager.autoLogin()
            handleNotificationIntent(intent)
        }
    }

    private fun setSmartOrientation() {
        val screenWidthDp = resources.configuration.smallestScreenWidthDp

        requestedOrientation = if (screenWidthDp >= 600) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
