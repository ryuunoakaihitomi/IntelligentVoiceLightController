package github.ryuunoakaihitomi.ivlc

import android.content.Context
import android.media.AudioManager
import androidx.core.content.getSystemService

private const val STREAM_TYPE = AudioManager.STREAM_MUSIC

private fun Context.getAudioManager() = getSystemService<AudioManager>()

fun Context.getVolume() = getAudioManager()?.getStreamVolume(STREAM_TYPE) ?: 1

fun Context.setVolume(volumeIndex: Int) {
    getAudioManager()?.run {
        setStreamVolume(STREAM_TYPE, volumeIndex, AudioManager.FLAG_PLAY_SOUND)
    }
}

fun Context.setMaxVolume() {
    getAudioManager()?.run { setVolume(getStreamMaxVolume(STREAM_TYPE)) }
}