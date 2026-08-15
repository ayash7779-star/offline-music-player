package com.example.musicplayer.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.data.AudioItem

/** Loads all on-device audio files via MediaStore. */
object AudioLoader {

    fun loadAll(ctx: Context): List<AudioItem> {
        val out = ArrayList<AudioItem>()
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val sel = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        ctx.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            proj, sel, null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { c ->
            val artUri = Uri.parse("content://media/external/audio/albumart")
            while (c.moveToNext()) {
                val id = c.getLong(0)
                out += AudioItem(
                    uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                    ),
                    title = c.getString(1) ?: "Unknown",
                    artist = c.getString(2) ?: "Unknown",
                    album = c.getString(3) ?: "Unknown",
                    durationMs = c.getLong(4),
                    albumArt = ContentUris.withAppendedId(artUri, c.getLong(5))
                )
            }
        }
        return out
    }
}
