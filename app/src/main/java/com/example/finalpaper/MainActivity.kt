package com.example.finalpaper

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
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
            FinalPaperTheme {
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE
                        )
                        imageCaptureMode = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
                    }
                }
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController)
                    }

                    composable(Screen.Magnifier.route) {
                        MagnifierScreen(controller)
                    }

                    composable(Screen.Navigation.route) {
                        MapScreen(navController)
                    }
                }
            }
        }
    }

}


