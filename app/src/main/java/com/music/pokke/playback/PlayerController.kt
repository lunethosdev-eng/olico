package com.music.pokke.playback

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.pokke.data.catalog.SilentHarvest
import com.music.pokke.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayerController {
    private var player: ExoPlayer? = null

    private val _current = MutableStateFlow<Song?>(null)
    val current: StateFlow<Song?> = _current.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _playing = MutableStateFlow(false)
    val playing: StateFlow<Boolean> = _playing.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playing.value = isPlaying
        }
    }

    fun bindPlayer(p: ExoPlayer?) {
        player?.removeListener(listener)
        player = p
        p?.addListener(listener)
        _playing.value = p?.isPlaying == true
    }

    fun play(context: Context, song: Song, queue: List<Song> = emptyList()) {
        val url = song.audioUrl ?: return
        SilentHarvest.enqueue(song.title, song.artist, song.youtubeId)
        ContextCompat.startForegroundService(
            context.applicationContext,
            Intent(context, PlaybackService::class.java),
        )
        val p = player ?: ExoPlayer.Builder(context.applicationContext).build().also { bindPlayer(it) }
        _current.value = song
        if (queue.isNotEmpty()) _queue.value = queue
        val meta = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setArtworkUri(song.coverUrl?.let { android.net.Uri.parse(it) })
            .build()
        p.setMediaItem(
            MediaItem.Builder().setUri(url).setMediaId(song.id).setMediaMetadata(meta).build(),
        )
        p.prepare()
        p.play()
    }

    fun toggle() {
        player?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    fun seek(ms: Long) {
        player?.seekTo(ms)
    }

    fun pollPosition() {
        _positionMs.value = player?.currentPosition ?: 0L
    }
}
