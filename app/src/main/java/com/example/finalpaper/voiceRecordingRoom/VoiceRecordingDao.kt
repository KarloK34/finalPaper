package com.example.finalpaper.voiceRecordingRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VoiceRecordingDao {
    @Insert
    suspend fun insert(voiceRecording: VoiceRecording)

    @Delete
    suspend fun delete(voiceRecording: VoiceRecording)

    @Query("SELECT * FROM voicerecording WHERE latitude BETWEEN :latMin AND :latMax AND longitude BETWEEN :lngMin AND :lngMax")
    suspend fun getMessagesNearLocation(latMin: Double, latMax: Double, lngMin: Double, lngMax: Double): List<VoiceRecording>

    @Query("SELECT * FROM voicerecording")
    suspend fun getVoiceRecordings() : List<VoiceRecording>
}