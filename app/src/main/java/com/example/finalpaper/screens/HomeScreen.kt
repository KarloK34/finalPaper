package com.example.finalpaper.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalpaper.audioUtilities.TextToSpeechController

@Composable
fun HomeScreen(
    navController: NavController,
    ttsController: TextToSpeechController,
    isColorBlind: Boolean,
    onToggleTheme: () -> Unit
) {
    val screenSize = LocalConfiguration.current.screenHeightDp.dp
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = {
                ttsController.speakInterruptingly("Magnifier")
                navController.navigate(Screen.Magnifier.route)
            },
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSize * 0.45f)
                .padding(5.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Text(text = "MAGNIFIER", fontSize = 50.sp)
        }
        FloatingActionButton(
            onClick = {
                ttsController.speakInterruptingly("Navigation")
                navController.navigate(Screen.Navigation.route)
            },
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSize * 0.45f)
                .padding(5.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(text = "NAVIGATION", fontSize = 50.sp)
        }
        FloatingActionButton(
            onClick = onToggleTheme,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSize * 0.1f)
                .padding(5.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = if (isColorBlind) "SWITCH TO REGULAR THEME" else "SWITCH TO COLOR BLIND THEME",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


