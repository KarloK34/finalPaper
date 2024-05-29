package com.example.finalpaper.voiceRecordingRoom

import java.io.File

data class VoiceRecordingState(
    val voiceRecordings: List<VoiceRecording> = emptyList(),
    val fileName: String = "",
    val audioFile: File? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val isAddingVoiceRecording: Boolean = false
)
