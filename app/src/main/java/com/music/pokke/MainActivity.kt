package com.music.pokke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.music.pokke.data.auth.KokoroAuth
import com.music.pokke.data.lyrics.LyricsApi
import com.music.pokke.data.model.DownloadProgress
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem
import com.music.pokke.data.plays.PlayTracker
import com.music.pokke.data.plays.TrendsRepository
import com.music.pokke.data.settings.AppSettings
import com.music.pokke.data.ukiyo.UkiyoApi
import com.music.pokke.data.youtube.YouTubeMusicClient
import com.music.pokke.data.youtube.YtStreamResolver
import com.music.pokke.playback.PlayerController
import com.music.pokke.ui.account.AccountScreen
import com.music.pokke.ui.account.AdminScreen
import com.music.pokke.ui.account.OnboardingArtistsScreen
import com.music.pokke.ui.home.HomeScreen
import com.music.pokke.ui.library.LibraryScreen
import com.music.pokke.ui.lyrics.LyricsScreen
import com.music.pokke.ui.player.FullPlayerScreen
import com.music.pokke.ui.player.MiniPlayer
import com.music.pokke.ui.player.PokkeBottomNav
import com.music.pokke.ui.search.SearchScreen
import com.music.pokke.ui.settings.SettingsScreen
import com.music.pokke.ui.settings.SpotifyConnectScreen
import com.music.pokke.ui.theme.PokkeTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokkeTheme {
                val nav = rememberNavController()
                var recent by remember { mutableStateOf<List<Song>>(emptyList()) }
                var trends by remember { mutableStateOf<List<TrendItem>>(emptyList()) }
                var query by remember { mutableStateOf("") }
                var results by remember { mutableStateOf<List<Song>>(emptyList()) }
                var progress by remember { mutableStateOf<DownloadProgress?>(null) }
                var showQueue by remember { mutableStateOf(false) }
                var lyricLine by remember { mutableStateOf<String?>(null) }
                var syncedLyrics by remember { mutableStateOf<List<Pair<Long, String>>>(emptyList()) }
                var showAudioSheet by remember { mutableStateOf(false) }
                var showMoreMenu by remember { mutableStateOf(false) }
                var sleepMinutes by remember { mutableStateOf(0) }

                val current by PlayerController.current.collectAsState()
                val playing by PlayerController.playing.collectAsState()
                val position by PlayerController.positionMs.collectAsState()
                val duration by PlayerController.durationMs.collectAsState()
                val volume by PlayerController.volume.collectAsState()
                val yt by AppSettings.youtubeEnabled.collectAsState()
                val glass by AppSettings.liquidGlass.collectAsState()

                LaunchedEffect(Unit) {
                    recent = runCatching { UkiyoApi.recentSongs() }.getOrDefault(emptyList())
                    val fromKokoro = runCatching { TrendsRepository.load() }.getOrDefault(emptyList())
                    trends = fromKokoro.ifEmpty {
                        runCatching { UkiyoApi.trends() }.getOrDefault(emptyList())
                    }
                }

                LaunchedEffect(playing) {
                    while (true) {
                        PlayerController.pollPosition()
                        delay(250)
                    }
                }

                LaunchedEffect(current?.id) {
                    lyricLine = null
                    syncedLyrics = emptyList()
                    val s = current ?: return@LaunchedEffect
                    val lyr = runCatching {
                        LyricsApi.fetch(s.title, s.artist, s.durationSeconds)
                    }.getOrNull()
                    syncedLyrics = lyr?.synced.orEmpty()
                    lyricLine = syncedLyrics.firstOrNull()?.second
                        ?: lyr?.plain?.lineSequence()?.firstOrNull()
                }

                LaunchedEffect(position, syncedLyrics) {
                    if (syncedLyrics.isEmpty()) return@LaunchedEffect
                    val line = syncedLyrics.lastOrNull { it.first <= position }?.second
                    if (line != null) lyricLine = line
                }

                LaunchedEffect(query, yt) {
                    if (query.length < 2) {
                        results = emptyList()
                        progress = null
                        return@LaunchedEffect
                    }
                    delay(350)
                    val ukiyoHits = runCatching { UkiyoApi.search(query) }.getOrDefault(emptyList())
                    val ytHits = if (yt) {
                        runCatching { YouTubeMusicClient.search(query) }.getOrDefault(emptyList())
                    } else emptyList()
                    results = (ukiyoHits + ytHits).distinctBy { it.youtubeId ?: it.id }
                    progress = null
                }

                fun play(song: Song) {
                    if (song.source == "youtube") YouTubeMusicClient.noteListen(song)
                    PlayTracker.record(song)
                    MainScope().launch {
                        var toPlay = song
                        if (toPlay.audioUrl.isNullOrBlank()) {
                            val vid = song.youtubeId ?: song.id
                            val ytUrl = if (song.source == "youtube" || !song.youtubeId.isNullOrBlank()) {
                                runCatching { YtStreamResolver.audioUrl(vid) }.getOrNull()
                            } else null
                            if (!ytUrl.isNullOrBlank()) {
                                toPlay = toPlay.copy(audioUrl = ytUrl)
                            } else {
                                val q = song.title + " " + song.artist
                                val hit = runCatching {
                                    UkiyoApi.search(q).firstOrNull { !it.audioUrl.isNullOrBlank() }
                                }.getOrNull()
                                if (hit != null) toPlay = hit.copy(title = song.title, artist = song.artist)
                            }
                        }
                        PlayerController.play(this@MainActivity, toPlay, recent)
                    }
                    nav.navigate("player")
                }

                val back by nav.currentBackStackEntryAsState()
                val route = back?.destination?.route
                val tabs = setOf("home", "search", "library")
                val showBar = route in tabs

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBar,
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { it } + fadeOut(),
                        ) {
                            Column {
                                current?.let { song ->
                                    MiniPlayer(
                                        song = song,
                                        playing = playing,
                                        liquidGlass = glass,
                                        onToggle = { PlayerController.toggle() },
                                        onNext = { PlayerController.next() },
                                        onOpen = { nav.navigate("player") },
                                    )
                                }
                                PokkeBottomNav(
                                    route = route ?: "home",
                                    liquidGlass = glass,
                                    onHome = { nav.navigate("home") { launchSingleTop = true } },
                                    onLibrary = { nav.navigate("library") { launchSingleTop = true } },
                                    onSearch = { nav.navigate("search") { launchSingleTop = true } },
                                )
                            }
                        }
                    },
                ) { padding ->
                    NavHost(nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                recent = recent,
                                trends = trends,
                                onPlay = ::play,
                                onSettings = { nav.navigate("settings") },
                                onProfile = { nav.navigate("account") },
                                contentPadding = padding,
                            )
                        }
                        composable("search") {
                            SearchScreen(query, { query = it }, results, progress, ::play, padding)
                        }
                        composable("library") {
                            LibraryScreen(recent, ::play, padding)
                        }
                        composable("settings") {
                            SettingsScreen(
                                isOwner = KokoroAuth.isOwner,
                                onAccount = { nav.navigate("account") },
                                onAdmin = { nav.navigate("admin") },
                                onSpotify = { nav.navigate("spotify") },
                                contentPadding = padding,
                            )
                        }
                        composable("account") {
                            AccountScreen(
                                onSpotify = { nav.navigate("spotify") },
                                onOnboarding = { nav.navigate("onboarding") },
                                contentPadding = padding,
                            )
                        }
                        composable("admin") { AdminScreen(padding) }
                        composable("spotify") { SpotifyConnectScreen(padding) }
                        composable("onboarding") {
                            OnboardingArtistsScreen(padding) { nav.popBackStack() }
                        }
                        composable("player") {
                            val song = current
                            val q by PlayerController.queue.collectAsState()
                            if (song == null) {
                                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                                    Text("Nada en reproducción")
                                }
                            } else {
                                FullPlayerScreen(
                                    song = song,
                                    queue = q.ifEmpty { listOf(song) },
                                    playing = playing,
                                    positionMs = position,
                                    durationMs = if (duration > 0) duration else (song.durationSeconds ?: 0) * 1000L,
                                    volume = volume,
                                    showQueue = showQueue,
                                    showAudioSheet = showAudioSheet,
                                    showMoreMenu = showMoreMenu,
                                    deviceName = "Este teléfono",
                                    lyricLine = lyricLine,
                                    sleepMinutes = sleepMinutes,
                                    onToggle = { PlayerController.toggle() },
                                    onSeek = { PlayerController.seek(it) },
                                    onVolume = { PlayerController.setVolume(it) },
                                    onPrev = { PlayerController.previous() },
                                    onNext = { PlayerController.next() },
                                    onLyrics = { nav.navigate("lyrics") },
                                    onQueueToggle = { showQueue = !showQueue },
                                    onDevice = { showAudioSheet = true },
                                    onDismissAudio = { showAudioSheet = false },
                                    onMore = { showMoreMenu = true },
                                    onDismissMore = { showMoreMenu = false },
                                    onSleep = { sleepMinutes = it },
                                    onShareLyrics = { nav.navigate("share_lyrics") },
                                    onClose = { nav.popBackStack() },
                                    onPlayIndex = { i ->
                                        val list = q.ifEmpty { listOf(song) }
                                        list.getOrNull(i)?.let { play(it) }
                                    },
                                )
                            }
                        }
                        composable("lyrics") {
                            LyricsScreen(current, position, padding)
                        }
                        composable("share_lyrics") {
                            com.music.pokke.ui.lyrics.ShareLyricsScreen(
                                song = current,
                                line = lyricLine,
                                contentPadding = padding,
                                onClose = { nav.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
