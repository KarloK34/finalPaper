package com.example.finalpaper.voiceRecordingRoom

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpaper.audioUtilities.AndroidAudioPlayer
import com.example.finalpaper.audioUtilities.AndroidAudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VoiceRecordingViewModel(
    private val dao: VoiceRecordingDao,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceRecordingState())
    val state: StateFlow<VoiceRecordingState> get() = _state

    private val recorder by lazy {
        AndroidAudioRecorder(context)
    }

    private val player by lazy {
        AndroidAudioPlayer(context)
    }

    private var audioFile: File? = null

    private var audioFileName: String = ""
    init {
        viewModelScope.launch {
            val recordings = withContext(Dispatchers.IO) {
                dao.getVoiceRecordings()
            }
            _state.value = VoiceRecordingState(voiceRecordings = recordings)
        }
    }
    fun startRecording(context: Context) {
        audioFileName = "voice_recording_${System.currentTimeMillis()}.3gp"
        File(context.filesDir,audioFileName).also {
            recorder.start(it)
            audioFile = it
        }
        _state.update { it.copy(
            isAddingVoiceRecording = true
        ) }
    }
    fun stopRecording() {
        recorder.stop()
        _state.update { it.copy(fileName = audioFileName, audioFile = audioFile) }
    }
    private fun getAudioFile(fileName: String): File {
        return File(context.filesDir, fileName)
    }
    fun playAudio(fileName: String) {
        val audioFile = getAudioFile(fileName)
        player.playFile(audioFile)
    }
    fun onEvent(event: VoiceRecordingEvent) {
        when(event) {
            is VoiceRecordingEvent.DeleteVoiceRecording -> {
                viewModelScope.launch {
                    dao.delete(event.voiceRecording)
                }
            }
            VoiceRecordingEvent.SaveVoiceRecording -> {
                val fileName = state.value.fileName
                val latitude = state.value.latitude
                val longitude = state.value.longitude
                val timestamp = state.value.timestamp

                if(fileName.isBlank() || latitude.isNaN() || longitude.isNaN()){
                    return
                }

                val voiceRecording = VoiceRecording(
                    fileName = fileName,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = timestamp
                )
                viewModelScope.launch {
                    dao.insert(voiceRecording)
                    val recordings = withContext(Dispatchers.IO) {
                        dao.getVoiceRecordings()
                    }
                    _state.value = VoiceRecordingState(voiceRecordings = recordings)
                }
                _state.update { it.copy(
                    isAddingVoiceRecording = false,
                    fileName = "",
                    audioFile = null,
                    latitude = 0.0,
                    longitude = 0.0,
                    timestamp = 0L
                ) }
            }
            is VoiceRecordingEvent.SetLatitude -> {
                _state.update { it.copy(
                    latitude = event.latitude
                ) }
            }
            is VoiceRecordingEvent.SetLongitude -> {
                _state.update { it.copy(
                    longitude = event.longitude
                ) }
            }
            is VoiceRecordingEvent.SetTimestamp -> {
                _state.update { it.copy(
                    timestamp = event.timestamp
                ) }
            }
        }
    }
}