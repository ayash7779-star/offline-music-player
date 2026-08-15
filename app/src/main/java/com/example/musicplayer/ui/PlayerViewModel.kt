package com.example.musicplayer.ui

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.AudioItem
import com.example.musicplayer.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class PlayerViewModel : ViewModel() {

    private val _items = MutableLiveData<List<AudioItem>>(emptyList())
    val items: LiveData<List<AudioItem>> = _items

    private val _nowPlaying = MutableLiveData<AudioItem?>()
    val nowPlaying: LiveData<AudioItem?> = _nowPlaying

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _position = MutableLiveData(0L)
    val position: LiveData<Long> = _position

    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> = _duration

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    fun connect(ctx: Context) {
        if (controller != null) return
        val token = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(ctx, token).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(p: Boolean) = _isPlaying.postValue(p)
                override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                    val idx = controller?.currentMediaItemIndex ?: 0
                    _nowPlaying.postValue(_items.value?.getOrNull(idx))
                    _duration.postValue(controller?.duration ?: 0)
                }
            })
            tick()
        }, Executor { r -> r.run() })
    }

    private fun tick() = viewModelScope.launch {
        while (true) {
            controller?.let {
                _position.postValue(it.currentPosition)
                _duration.postValue(it.duration)
            }
            delay(500)
        }
    }

    fun setQueue(items: List<AudioItem>) {
        _items.postValue(items)
        controller?.apply {
            setMediaItems(items.map { it.toMediaItem() })
            prepare()
        }
    }

    fun togglePlay() {
        val p = controller ?: return
        if (p.isPlaying) p.pause() else p.play()
    }

    fun next() = controller?.seekToNextMediaItem()
    fun prev() = controller?.seekToPreviousMediaItem()
    fun seekTo(pos: Long) = controller?.seekTo(pos)
    fun setVolume(v: Float) { controller?.volume = v }
    fun setBassBoost(level: Int) {
        // Bass boost via ExoPlayer requires audio processing; level 0..1000 (mB)
        // Simplified stub — integrate androidx.media3 Effect pipeline in production.
    }

    override fun onCleared() {
        controller?.release()
        super.onCleared()
    }

    companion object {
        @Volatile private var INSTANCE: PlayerViewModel? = null
        fun get(ctx: Context): PlayerViewModel =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlayerViewModel().also {
                    INSTANCE = it
                    it.connect(ctx.applicationContext)
                }
            }
    }
}

private fun AudioItem.toMediaItem() = MediaItem.Builder()
    .setUri(uri)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(albumArt)
            .build()
    ).build()
