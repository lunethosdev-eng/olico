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
            val p = player ?: return
            if (p.duration > 0) _durationMs.value = p.duration
            _playing.value = p.isPlaying
            if (playbackState == Player.STATE_ENDED) {
                next()
            }
        }
    }

    fun ensurePlayer(context: Context): ExoPlayer {
        appContext = context.applicationContext
        player?.let { return it }
        val p = ExoPlayer.Builder(context.applicationContext).build()
        bindPlayer(p)
        return p
    }

    fun bindPlayer(p: ExoPlayer?) {
        player?.removeListener(listener)
        player = p
        p?.addListener(listener)
        _playing.value = p?.isPlaying == true
    }

    fun play(context: Context, song: Song, queue: List<Song> = emptyList()) {
        val url = song.audioUrl
        if (url.isNullOrBlank()) return
        appContext = context.applicationContext
        SilentHarvest.enqueue(song.title, song.artist, song.youtubeId)
        runCatching {
            ContextCompat.startForegroundService(
                context.applicationContext,
                Intent(context, PlaybackService::class.java),
            )
        }
        val p = ensurePlayer(context)
        _current.value = song
        if (queue.isNotEmpty()) {
            _queue.value = queue
        } else {
            val q = _queue.value.toMutableList()
            if (q.none { it.id == song.id }) q.add(0, song)
            _queue.value = q
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
        _positionMs.value = 0L
    }

    fun toggle() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            _playing.value = false
        } else {
            p.playWhenReady = true
            p.play()
            _playing.value = true
        }
    }

    fun seek(ms: Long) {
        val p = player ?: return
        p.seekTo(ms.coerceAtLeast(0L))
        _positionMs.value = ms
    }

    fun setVolume(v: Float) {
        val nv = v.coerceIn(0f, 1f)
        player?.volume = nv
        _volume.value = nv
    }

    fun next() {
        val ctx = appContext ?: return
        val q = _queue.value
        if (q.isEmpty()) return
        val cur = _current.value
        val i = if (cur == null) -1 else q.indexOfFirst { it.id == cur.id }
        val nextSong = when {
            i < 0 -> q.firstOrNull()
            i + 1 < q.size -> q[i + 1]
            else -> null
        } ?: return
        if (nextSong.audioUrl.isNullOrBlank()) return
        play(ctx, nextSong, q)
    }

    fun previous() {
        val ctx = appContext ?: return
        val q = _queue.value
        if (q.isEmpty()) return
        val cur = _current.value ?: return
        val i = q.indexOfFirst { it.id == cur.id }
        if (i <= 0) {
            seek(0)
            return
        }
        val prev = q[i - 1]
        if (prev.audioUrl.isNullOrBlank()) return
        play(ctx, prev, q)
    }

    fun pollPosition() {
        val p = player ?: return
        _positionMs.value = p.currentPosition.coerceAtLeast(0L)
        if (p.duration > 0) _durationMs.value = p.duration
        _playing.value = p.isPlaying
    }
}
