package com.example.finalpaper

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.locationUtilities.PlacesClientProvider
import com.example.finalpaper.screens.HomeScreen
import com.example.finalpaper.screens.MagnifierScreen
import com.example.finalpaper.screens.MapScreen
import com.example.finalpaper.screens.Screen
import com.example.finalpaper.ui.theme.FinalPaperTheme
import com.example.finalpaper.voiceRecordingRoom.AppDatabase

class MainActivity : ComponentActivity() {

    object DatabaseProvider {
        private var db: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database"
                ).build()
            }
            return db!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlacesClientProvider.getClient(this)
        setContent {
            var isColorBlind by remember { mutableStateOf(false) }

            FinalPaperTheme(isColorBlind = isColorBlind) {
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE
                        )
                        imageCaptureMode = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY

                        val maxZoomRatio = cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f
                        setZoomRatio(maxZoomRatio)
                    }
                }
                val navController = rememberNavController()
                val ttsController = remember { TextToSpeechController(applicationContext) }

                DisposableEffect(Unit) {
                    onDispose {
                        ttsController.shutdown()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController,
                            ttsController,
                            isColorBlind = isColorBlind,
                            onToggleTheme = {
                                isColorBlind = !isColorBlind
                                if (!isColorBlind) ttsController.speakInterruptingly("Regular theme") else ttsController.speakInterruptingly(
                                    "Color blind theme"
                                )
                            })
                    }

                    composable(Screen.Magnifier.route) {
                        MagnifierScreen(controller, ttsController)
                    }

                    composable(Screen.Navigation.route) {
                        MapScreen(ttsController)
                    }
                }
            }
        }
    }

}


