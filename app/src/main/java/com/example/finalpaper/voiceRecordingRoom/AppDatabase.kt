package com.example.finalpaper.voiceRecordingRoom

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [VoiceRecording::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voiceRecordingDao(): VoiceRecordingDao
}