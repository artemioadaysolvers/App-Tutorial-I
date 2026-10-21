package com.retrolab.casiofx2200

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/** Short, preloaded button feedback, played entirely from Kotlin. */
class ButtonSound(context: Context) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        .build()
    private var sampleId = 0
    private var ready = false
    private var released = false
    private val streams = ArrayDeque<Int>()

    init {
        pool.setOnLoadCompleteListener { _, id, status ->
            if (!released && id == sampleId) ready = status == 0
        }
        sampleId = pool.load(context, R.raw.button_click, 1)
    }

    fun play() {
        if (!ready || released) return
        val stream = pool.play(sampleId, 0.7f, 0.7f, 1, 0, 1f)
        if (stream != 0) {
            if (streams.size == 4) streams.removeFirst()
            streams.addLast(stream)
        }
    }

    fun stop() {
        if (released) return
        streams.forEach(pool::stop)
        streams.clear()
    }

    fun release() {
        if (released) return
        stop()
        released = true
        ready = false
        pool.release()
    }
}
