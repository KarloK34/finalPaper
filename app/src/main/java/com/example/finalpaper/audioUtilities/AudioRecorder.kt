package com.example.finalpaper.audioUtilities

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}