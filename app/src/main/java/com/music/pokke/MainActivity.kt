package com.music.pokke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.music.pokke.data.plays.PlayTracker
import com.music.pokke.data.youtube.YouTubeMusicClient
import com.music.pokke.data.model.DownloadProgress
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem
import com.music.pokke.data.settings.AppSettings
import com.music.pokke.data.ukiyo.UkiyoApi
import com.music.pokke.playback.PlayerController
import com.music.pokke.ui.account.AccountScreen
import com.music.pokke.ui.account.AdminScreen
import com.music.pokke.ui.home.HomeScreen
import com.music.pokke.ui.library.LibraryScreen
import com.music.pokke.ui.lyrics.LyricsParser
import com.music.pokke.ui.lyrics.LyricsScreen
import com.music.pokke.ui.player.FullPlayerScreen
import com.music.pokke.ui.player.MiniPlayer
import com.music.pokke.ui.search.SearchScreen
import com.music.pokke.ui.settings.SettingsScreen
import com.music.pokke.ui.theme.PokkeTheme
import kotlinx.coroutines.delay

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

                val current by PlayerController.current.collectAsState()
                val playing by PlayerController.playing.collectAsState()
                val position by PlayerController.positionMs.collectAsState()
                val yt by AppSettings.youtubeEnabled.collectAsState()

                LaunchedEffect(Unit) {
                    recent = runCatching { UkiyoApi.recentSongs() }.getOrDefault(emptyList())
                    trends = runCatching { UkiyoApi.trends() }.getOrDefault(emptyList())
                }

                LaunchedEffect(playing) {
                    while (playing) {
                        PlayerController.pollPosition()
                        delay(250)
                    }
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
                    val found = (ukiyoHits + ytHits).distinctBy { it.youtubeId ?: it.id }
                    results = found
                    if (!yt && found.isEmpty()) {
                        var n = 0
                        while (n < 30) {
                            val p = runCatching { UkiyoApi.progress(query.trim()) }.getOrNull()
                            if (p != null) {
                                progress = p
                                if (p.done || p.error != null) break
                            }
                            delay(500)
                            n++
                            results = runCatching { UkiyoApi.search(query) }.getOrDefault(emptyList())
                            if (results.isNotEmpty()) {
                                progress = null
                                break
                            }
                        }
                    } else progress = null
                }

                fun play(song: Song) {
                    if (song.source == "youtube") YouTubeMusicClient.noteListen(song)
                    PlayTracker.record(song)
                    PlayerController.play(this@MainActivity, song, recent)
                    nav.navigate("player")
                }

                val back by nav.currentBackStackEntryAsState()
                val route = back?.destination?.route
                val tabs = listOf("home", "search", "library", "settings")
                val showBar = route in tabs

                Scaffold(
                    bottomBar = {
                        if (showBar) {
                            Column {
                                current?.let { song ->
                                    MiniPlayer(
                                        song = song,
                                        playing = playing,
                                        onToggle = { PlayerController.toggle() },
                                        onOpen = { nav.navigate("player") },
                                    )
                                }
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = route == "home",
                                        onClick = { nav.navigate("home") },
                                        icon = { Icon(Icons.Rounded.Home, null) },
                                        label = { Text("Home") },
                                    )
                                    NavigationBarItem(
                                        selected = route == "search",
                                        onClick = { nav.navigate("search") },
                                        icon = { Icon(Icons.Rounded.Search, null) },
                                        label = { Text("Search") },
                                    )
                                    NavigationBarItem(
                                        selected = route == "library",
                                        onClick = { nav.navigate("library") },
                                        icon = { Icon(Icons.Rounded.LibraryMusic, null) },
                                        label = { Text("Library") },
                                    )
                                    NavigationBarItem(
                                        selected = route == "settings",
                                        onClick = { nav.navigate("settings") },
                                        icon = { Icon(Icons.Rounded.Settings, null) },
                                        label = { Text("Settings") },
                                    )
                                }
                            }
                        }
                    },
                ) { padding ->
                    NavHost(nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(recent, trends, ::play, padding)
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
                                contentPadding = padding,
                            )
                        }
                        composable("account") { AccountScreen(padding) }
                        composable("admin") { AdminScreen(padding) }
                        composable("player") {
                            val song = current
                            if (song == null) {
                                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                                    Text("Nothing playing")
                                }
                            } else {
                                FullPlayerScreen(
                                    song = song,
                                    playing = playing,
                                    positionMs = position,
                                    durationMs = (song.durationSeconds ?: 0) * 1000L,
                                    onToggle = { PlayerController.toggle() },
                                    onSeek = { PlayerController.seek(it) },
                                    onLyrics = { nav.navigate("lyrics") },
                                    onQueue = { /* v0.2 */ },
                                )
                            }
                        }
                        composable("lyrics") {
                            val lines = LyricsParser.parse(current?.syncedLyrics ?: current?.lyrics)
                            LyricsScreen(lines, position, Modifier.padding(padding))
                        }
                    }
                }
            }
        }
    }
}
