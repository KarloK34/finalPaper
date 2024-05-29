package com.example.finalpaper.voiceRecordingRoom

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity
data class VoiceRecording(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
