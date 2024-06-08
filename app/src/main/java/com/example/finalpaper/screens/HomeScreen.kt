package com.example.finalpaper.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalpaper.audioUtilities.TextToSpeechController

@Composable
fun HomeScreen(
    navController: NavController,
    ttsController: TextToSpeechController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            ttsController.speak("Magnifier")
            navController.navigate(Screen.Magnifier.route)
        }) {
            Text(text = "MAGNIFIER")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = {
            ttsController.speak("Navigation")
            navController.navigate(Screen.Navigation.route)
        }) {
            Text(text = "NAVIGATION")
        }
    }
}


