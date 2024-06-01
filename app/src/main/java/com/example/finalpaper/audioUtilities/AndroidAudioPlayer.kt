package com.example.finalpaper.audioUtilities

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File
import java.io.IOException

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null
    private val audioQueue = mutableListOf<File>()

    init {
        setupPlayer()
    }

    override fun playFile(file: File) {
        audioQueue.add(file)
        if (player?.isPlaying == false) {
            playNext()
        }
    }

    private fun setupPlayer() {
        player = MediaPlayer().apply {
            setOnCompletionListener {
                playNext()
            }
        }
    }

    private fun playNext() {
        if (audioQueue.isNotEmpty()) {
            val file = audioQueue.removeAt(0)
            try {
                player?.reset()
                player?.setDataSource(context, file.toUri())
                player?.prepare()
                player?.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}