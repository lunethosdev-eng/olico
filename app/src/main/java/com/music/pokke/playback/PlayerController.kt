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
    private var appContext: Context? = null

    private val _current = MutableStateFlow<Song?>(null)
    val current: StateFlow<Song?> = _current.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _playing = MutableStateFlow(false)
    val playing: StateFlow<Boolean> = _playing.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _volume = MutableStateFlow(1f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playing.value = isPlaying
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            _durationMs.value = player?.duration?.coerceAtLeast(0L) ?: 0L
            _playing.value = player?.isPlaying == true
        }
    }

    fun bindPlayer(p: ExoPlayer?) {
        player?.removeListener(listener)
        player = p
        p?.addListener(listener)
        _playing.value = p?.isPlaying == true
    }

    fun play(context: Context, song: Song, queue: List<Song> = emptyList()) {
        appContext = context.applicationContext
        val url = song.audioUrl
        if (url.isNullOrBlank()) return
        SilentHarvest.enqueue(song.title, song.artist, song.youtubeId)
        ContextCompat.startForegroundService(
            context.applicationContext,
            Intent(context, PlaybackService::class.java),
        )
        val p = player ?: ExoPlayer.Builder(context.applicationContext).build().also { bindPlayer(it) }
        _current.value = song
        if (queue.isNotEmpty()) _queue.value = queue else if (_queue.value.none { it.id == song.id }) {
            _queue.value = listOf(song) + _queue.value
        }
        val meta = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setArtworkUri(song.coverUrl?.let { android.net.Uri.parse(it) })
            .build()
        p.setMediaItem(
            MediaItem.Builder().setUri(url).setMediaId(song.id).setMediaMetadata(meta).build(),
        )
        p.prepare()
        p.playWhenReady = true
        p.play()
        _playing.value = true
    }

    fun toggle() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            _playing.value = false
        } else {
            p.play()
            _playing.value = true
        }
    }

    fun seek(ms: Long) {
        player?.seekTo(ms.coerceAtLeast(0L))
        _positionMs.value = ms
    }

    fun setVolume(v: Float) {
        val nv = v.coerceIn(0f, 1f)
        player?.volume = nv
        _volume.value = nv
    }

    fun next() {
        val q = _queue.value
        val cur = _current.value ?: return
        val i = q.indexOfFirst { it.id == cur.id }
        val next = q.getOrNull(i + 1) ?: return
        appContext?.let { play(it, next, q) }
    }

    fun previous() {
        val q = _queue.value
        val cur = _current.value ?: return
        val i = q.indexOfFirst { it.id == cur.id }
        val prev = q.getOrNull(i - 1) ?: return
        appContext?.let { play(it, prev, q) }
    }

    fun pollPosition() {
        val p = player ?: return
        _positionMs.value = p.currentPosition
        val d = p.duration
        if (d > 0) _durationMs.value = d
        _playing.value = p.isPlaying
    }
}
