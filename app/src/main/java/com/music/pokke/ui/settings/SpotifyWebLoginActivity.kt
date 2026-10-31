package com.music.pokke.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.music.pokke.data.spotify.SpotifyAuth

/**
 * Spotify login in WebView — harvests only THIS device's sp_dc cookie.
 */
class SpotifyWebLoginActivity : ComponentActivity() {
    private var saved = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val web = WebView(this)
        setContentView(web)

        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        cm.setAcceptThirdPartyCookies(web, true)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            userAgentString = userAgentString.replace("; wv", "")
        }
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (saved) return
                val raw = cm.getCookie("https://open.spotify.com")
                    ?: cm.getCookie("https://accounts.spotify.com")
                    ?: return
                val spdc = raw.split(";")
                    .map { it.trim() }
                    .firstOrNull { it.startsWith("sp_dc=") }
                    ?.substringAfter("sp_dc=")
                    ?.trim()
                if (!spdc.isNullOrBlank() && spdc.length > 30) {
                    SpotifyAuth.setSpDc(spdc)
                    saved = true
                    Toast.makeText(this@SpotifyWebLoginActivity, "Spotify conectado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        // Start at login; after auth Spotify sets cookies on open.spotify.com
        web.loadUrl("https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F")
    }
}
