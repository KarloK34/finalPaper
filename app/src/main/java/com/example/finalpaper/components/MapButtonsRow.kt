package com.example.finalpaper.components

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.finalpaper.R
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
    dao: VoiceRecordingDao,
    ttsController: TextToSpeechController
) {
    var nearbyRecordings by remember { mutableStateOf<List<VoiceRecording>>(emptyList()) }

    val categories = arrayOf("RESTAURANT", "CAFE'S", "SCHOOL")
    var selectedCategory by remember { mutableStateOf("RESTAURANT") }

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setTitle("SELECT POINT OF INTEREST CATEGORY TO ANNOUNCE")
        .setSingleChoiceItems(categories, 0) { _, which ->
            selectedCategory = categories[which]
            ttsController.speakInterruptingly(categories[which])
        }
        .setPositiveButton("Announce") { _, _ ->
            ttsController.speakInterruptingly("Announce")
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
                    }
                }
            }
            isSurroundingButtonClicked.value = !isSurroundingButtonClicked.value
        }
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            FloatingActionButton(onClick = {
                selectedCategory = categories[0]
                ttsController.speakInterruptingly("Select POI category to announce")
                dialog.show()
            }, modifier = Modifier.padding(10.dp)) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Point of interest categories"
                )
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Column(
                modifier = Modifier.padding(bottom = 95.dp),
                horizontalAlignment = Alignment.End
            ) {
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
                            if (state.isPlayingRecording) {
                                voiceRecordingViewModel.stopAudio()
                            } else {
                                if (nearbyRecordings.isNotEmpty()) {
                                    nearbyRecordings.forEach {
                                        voiceRecordingViewModel.playAudio(it.fileName)
                                    }
                                }
                            }
                        }, modifier = Modifier
                            .padding(bottom = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_hearing_24),
                            contentDescription = "Hear voice recording"
                        )
                    }
                }
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
                    }
                ) {
                    Icon(
                        painter = if (state.isAddingVoiceRecording) painterResource(id = R.drawable.baseline_stop_24)
                        else painterResource(
                            id = R.drawable.baseline_mic_24
                        ),
                        contentDescription = "Add voice recording"
                    )
                }
                FloatingActionButton(
                    onClick = {
                        ttsController.speak("Surroundings")
                        isSurroundingButtonClicked.value = !isSurroundingButtonClicked.value
                    },
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Surroundings")
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
            if (pois.isEmpty()) ttsController.speak("There is no $category near you")
            pois.forEach { poi ->
                ttsController.speak("You are near ${poi.name}")
            }
        }
    }
}