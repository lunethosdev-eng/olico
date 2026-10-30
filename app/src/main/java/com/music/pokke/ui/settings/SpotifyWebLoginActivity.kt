package com.music.pokke.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import com.music.pokke.data.spotify.SpotifyAuth

/**
 * User logs into open.spotify.com; we read sp_dc from CookieManager (their session only).
 * Never hardcode shared cookies in the APK.
 */
class SpotifyWebLoginActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val web = WebView(this)
        setContentView(web)
        CookieManager.getInstance().setAcceptCookie(true)
        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies = CookieManager.getInstance().getCookie("https://open.spotify.com") ?: return
                val spdc = cookies.split(";")
                    .map { it.trim() }
                    .firstOrNull { it.startsWith("sp_dc=") }
                    ?.substringAfter("sp_dc=")
                if (!spdc.isNullOrBlank() && spdc.length > 20) {
                    SpotifyAuth.setSpDc(spdc)
                    finish()
                }
            }
        }
        web.loadUrl("https://accounts.spotify.com/en/login")
    }
}
