package com.example.finalpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalpaper.screens.HomeScreen
import com.example.finalpaper.screens.MagnifierScreen
import com.example.finalpaper.screens.MapScreen
import com.example.finalpaper.screens.Screen
import com.example.finalpaper.ui.theme.FinalPaperTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalPaperTheme {
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
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
                        MapScreen()
                    }
                }
            }
        }
    }

}


