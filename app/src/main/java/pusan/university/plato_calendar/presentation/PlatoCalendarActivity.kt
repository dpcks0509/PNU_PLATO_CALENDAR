package pusan.university.plato_calendar.presentation

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
import pusan.university.plato_calendar.data.local.database.SettingsDataStore
import pusan.university.plato_calendar.presentation.common.component.AnimatedToast
import pusan.university.plato_calendar.presentation.common.component.LoadingIndicator
import pusan.university.plato_calendar.presentation.common.component.dialog.Dialog
import pusan.university.plato_calendar.presentation.common.component.dialog.DialogState.Companion.rememberDialogState
import pusan.university.plato_calendar.presentation.common.component.dialog.content.DialogContent
import pusan.university.plato_calendar.presentation.common.eventbus.WidgetEvent
import pusan.university.plato_calendar.presentation.common.eventbus.WidgetEventBus
import pusan.university.plato_calendar.presentation.common.manager.LoadingManager
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.manager.ScheduleManager
import pusan.university.plato_calendar.presentation.common.manager.SettingsManager
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarBottomBar
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarNavHost
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_NOTIFICATION_ID
import pusan.university.plato_calendar.presentation.common.notification.AlarmScheduler.Companion.EXTRA_SCHEDULE_ID
import pusan.university.plato_calendar.presentation.common.notification.NotificationHelper
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import pusan.university.plato_calendar.presentation.common.theme.White
import pusan.university.plato_calendar.presentation.widget.callback.OpenNewScheduleCallback
import pusan.university.plato_calendar.presentation.widget.callback.OpenScheduleDetailCallback
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var scheduleManager: ScheduleManager

    @Inject
    lateinit var loadingManager: LoadingManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSmartOrientation()
        if (savedInstanceState == null) {
            initializeApp()
        }

        setContent {
            val dialogState = rememberDialogState()
            val navController = rememberNavController()
            val isLoading by loadingManager.isLoading.collectAsStateWithLifecycle()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                lifecycleScope.launch {
                    settingsManager.setNotificationsEnabled(isGranted)
                }

                if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    dialogState.show(
                        DialogContent.NotificationPermission {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            startActivity(intent)
                        },
                    )
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

                    Dialog(state = dialogState)

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
            val selectedDate = intent.getStringExtra(OpenScheduleDetailCallback.EXTRA_SELECTED_DATE)
            lifecycleScope.launch {
                WidgetEventBus.sendEvent(WidgetEvent.OpenSchedule(scheduleId, selectedDate))
            }
        }

        if (intent.action == OpenNewScheduleCallback.ACTION_OPEN_NEW_SCHEDULE) {
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
