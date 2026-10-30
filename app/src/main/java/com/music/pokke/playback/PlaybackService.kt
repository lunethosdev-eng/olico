package com.music.pokke.playback

import android.app.PendingIntent
import android.content.Intent
import android.media.audiofx.Equalizer
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.music.pokke.MainActivity
import com.music.pokke.data.settings.AppSettings

@UnstableApi
class PlaybackService : MediaSessionService() {
    private var player: ExoPlayer? = null
    private var session: MediaSession? = null
    private var equalizer: Equalizer? = null

    override fun onCreate() {
        super.onCreate()
        val p = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .setSkipSilenceEnabled(AppSettings.skipSilence.value)
            .build()
        // Crossfade approximation: short pause-less gap via skip silence + future gapless
        player = p
        PlayerController.bindPlayer(p)
        attachEq(p.audioSessionId)
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        session = MediaSession.Builder(this, p).setSessionActivity(pi).build()
    }

    private fun attachEq(sessionId: Int) {
        runCatching {
            equalizer?.release()
            if (!AppSettings.equalizer.value || sessionId == 0) {
                equalizer = null
                return
            }
            val eq = Equalizer(0, sessionId).apply { enabled = true }
            // Mild presence boost as default preset
            val bands = eq.numberOfBands.toInt()
            for (i in 0 until bands) {
                val mid = (eq.bandLevelRange[0] + eq.bandLevelRange[1]) / 4
                eq.setBandLevel(i.toShort(), mid.toShort())
            }
            equalizer = eq
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onDestroy() {
        equalizer?.release()
        equalizer = null
        session?.run { player.release(); release() }
        session = null
        player = null
        PlayerController.bindPlayer(null)
        super.onDestroy()
    }
}
