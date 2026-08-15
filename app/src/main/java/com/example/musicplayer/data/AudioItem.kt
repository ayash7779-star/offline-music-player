package com.example.musicplayer.data

import android.net.Uri

data class AudioItem(
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArt: Uri?,
    val lyrics: String? = null
)
