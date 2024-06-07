package com.example.finalpaper.components

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.locationUtilities.POIRepository
import com.example.finalpaper.voiceRecordingRoom.VoiceRecording
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingDao
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingEvent
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingState
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MapButtonsRow(
    state: VoiceRecordingState,
    voiceRecordingViewModel: VoiceRecordingViewModel,
    currentLocation: LatLng? = null,
    context: Context,
    dao: VoiceRecordingDao
) {
    val ttsController = remember { TextToSpeechController(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsController.shutdown()
        }
    }
    var nearbyRecordings by remember { mutableStateOf<List<VoiceRecording>>(emptyList()) }

    val categories = arrayOf("RESTAURANT", "CAFE'S", "SCHOOL")
    var selectedCategory by remember { mutableStateOf("RESTAURANT") }

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setTitle("SELECT POINT OF INTEREST CATEGORY TO ANNOUNCE")
        .setSingleChoiceItems(categories, 0) { _, which ->
            selectedCategory = categories[which]
        }
        .setPositiveButton("Announce") { _, _ ->
            fetchAndAnnouncePOIs(selectedCategory, currentLocation, context, ttsController)
        }

    val dialog: AlertDialog = builder.create()

    val isSurroundingButtonClicked = remember { mutableStateOf(false) }

    LaunchedEffect(isSurroundingButtonClicked.value) {
        if (isSurroundingButtonClicked.value) {
            currentLocation.let { location ->
                val pois = POIRepository(context).getPOIs(location!!, 20)
                pois.forEach { poiLikelihood ->
                    poiLikelihood.place.let {
                        ttsController.speak("You are near ${it.name}")
                        it.name?.let { it1 -> Log.d("TEST", it1) }
                    }
                }
            }
            isSurroundingButtonClicked.value = !isSurroundingButtonClicked.value
        }
    }

    Row {
        FloatingActionButton(onClick = {
            selectedCategory = categories[0]
            dialog.show()
        }, modifier = Modifier.padding(start = 16.dp)) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Point of interest categories"
            )
        }
        FloatingActionButton(
            onClick = { isSurroundingButtonClicked.value = !isSurroundingButtonClicked.value },
            modifier = Modifier
                .padding(start = 16.dp, bottom = 30.dp)
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Surroundings")
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            FloatingActionButton(
                onClick = {
                    if (state.isAddingVoiceRecording) {
                        voiceRecordingViewModel.stopRecording()
                        voiceRecordingViewModel.onEvent(
                            VoiceRecordingEvent.SetLatitude(
                                currentLocation!!.latitude
                            )
                        )
                        voiceRecordingViewModel.onEvent(
                            VoiceRecordingEvent.SetLongitude(
                                currentLocation.longitude
                            )
                        )
                        voiceRecordingViewModel.onEvent(
                            VoiceRecordingEvent.SetTimestamp(
                                System.currentTimeMillis()
                            )
                        )
                        voiceRecordingViewModel.onEvent(VoiceRecordingEvent.SaveVoiceRecording)
                    } else {
                        voiceRecordingViewModel.startRecording(context)
                    }
                },
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 30.dp)
            ) {
                Icon(
                    imageVector = if (state.isAddingVoiceRecording) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Add voice recording"
                )
            }
            LaunchedEffect(key1 = currentLocation) {
                if (currentLocation != null) {
                    val latMin = currentLocation.latitude - 0.001
                    val latMax = currentLocation.latitude + 0.001
                    val lngMin = currentLocation.longitude - 0.001
                    val lngMax = currentLocation.longitude + 0.001
                    val recordings = withContext(Dispatchers.IO) {
                        dao.getMessagesNearLocation(latMin, latMax, lngMin, lngMax)
                    }
                    nearbyRecordings = recordings
                }
            }
            if (nearbyRecordings.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        nearbyRecordings.forEach {
                            voiceRecordingViewModel.playAudio(it.fileName)
                        }
                    }, modifier = Modifier
                        .padding(start = 16.dp, bottom = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Hear voice recording"
                    )
                }
            }
        }

    }
}

private fun fetchAndAnnouncePOIs(
    category: String,
    currentLocation: LatLng?,
    context: Context,
    ttsController: TextToSpeechController
) {
    if (currentLocation != null) {
        CoroutineScope(Dispatchers.Main).launch {
            val pois = POIRepository(context).getPOIsOfCategory(currentLocation, 200, category)
            pois.forEach { poi ->
                ttsController.speak("You are near ${poi.name}")
                Log.d("POI", poi.name)
            }
        }
    }
}
