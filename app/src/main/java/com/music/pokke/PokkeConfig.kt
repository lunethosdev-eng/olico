package com.music.pokke

/**
 * Pokke backends. Music API tunnel can change — update [MUSIC_API_BASE] or Admin.
 */
object PokkeConfig {
    /** Ukiyo music gateway (search, songs, progress, harvest). */
    const val MUSIC_API_BASE = "https://steve-inquire-satisfy-tear.trycloudflare.com"

    const val UKIYO_SUPABASE_URL = "https://esjoifsjljvymttinyhj.supabase.co"
    const val UKIYO_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVzam9pZnNqbGp2eW10dGlueWhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTEwMzYyMTAsImV4cCI6MjEwNjYxMjIxMH0.GKA3KyoMElVwLUQlL2ad82IPjRmwKmMJEGPTUNwbuVQ"

    const val KOKORO_URL = "https://ajbmpgnzkgtcmulocftd.supabase.co"
    const val KOKORO_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFqYm1wZ256a2d0Y211bG9jZnRkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTAzNDU2MjksImV4cCI6MjEwNTkyMTYyOX0.twsBnT9WBBILyXFuoQh7viI578yoNbeQztKHsr8DB8k"

    /** Owner email for Admin (password never in APK). */
    const val OWNER_EMAIL = "lunethos.dev@gmail.com"

    /**
     * Google OAuth Web client ID for Supabase Auth + Credential Manager.
     * Redirect in Google Cloud must include:
     * https://ajbmpgnzkgtcmulocftd.supabase.co/auth/v1/callback
     */
    const val GOOGLE_WEB_CLIENT_ID =
        "749672765922-vdcee0ha0tsrq00ca2n1733jsn0mucc4.apps.googleusercontent.com"

    /**
     * Discord Application Client ID (Developer Portal → Application).
     * Leave blank until created — presence stays off.
     */
    const val DISCORD_APPLICATION_ID = "1565886577434038312"
}
