package com.example.finalpaper.voiceRecordingRoom


sealed interface VoiceRecordingEvent {
    data object SaveVoiceRecording: VoiceRecordingEvent

    data class SetLatitude(val latitude: Double): VoiceRecordingEvent
    data class SetLongitude(val longitude: Double): VoiceRecordingEvent
    data class SetTimestamp(val timestamp: Long): VoiceRecordingEvent
    data class DeleteVoiceRecording(val voiceRecording: VoiceRecording): VoiceRecordingEvent
}