package com.music.pokke.data.auth

import com.music.pokke.PokkeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object KokoroAuth {
    private val json = Json { ignoreUnknownKeys = true }
    private val http = OkHttpClient()
    private val media = "application/json".toMediaType()

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    val isOwner: Boolean
        get() = _session.value?.email.equals(PokkeConfig.OWNER_EMAIL, ignoreCase = true) == true

    suspend fun signInEmail(email: String, password: String): Result<Session> = withContext(Dispatchers.IO) {
        runCatching {
            val body = """{"email":${email.q()},"password":${password.q()}}"""
            val req = Request.Builder()
                .url("${PokkeConfig.KOKORO_URL}/auth/v1/token?grant_type=password")
                .header("apikey", PokkeConfig.KOKORO_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(body.toRequestBody(media))
                .build()
            http.newCall(req).execute().use { resp ->
                val text = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) error(parseError(resp.code, text))
                toSession(text, email)
            }
        }
    }

    suspend fun signUpEmail(email: String, password: String): Result<Session> = withContext(Dispatchers.IO) {
        runCatching {
            if (password.length < 6) error("Password must be at least 6 characters")
            val body = """{"email":${email.q()},"password":${password.q()}}"""
            val req = Request.Builder()
                .url("${PokkeConfig.KOKORO_URL}/auth/v1/signup")
                .header("apikey", PokkeConfig.KOKORO_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(body.toRequestBody(media))
                .build()
            http.newCall(req).execute().use { resp ->
                val text = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) error(parseError(resp.code, text))
                runCatching { toSession(text, email) }.getOrElse {
                    signInEmail(email, password).getOrThrow()
                }
            }
        }
    }

    fun googleReady(): Boolean = PokkeConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()
    fun signOut() { _session.value = null }

    private fun toSession(text: String, emailFallback: String): Session {
        val tok = json.decodeFromString<TokenResponse>(text)
        if (tok.accessToken.isBlank()) error("No access token (email confirmation may be required)")
        return Session(
            accessToken = tok.accessToken,
            email = tok.user?.email ?: emailFallback,
            userId = tok.user?.id.orEmpty(),
        ).also { _session.value = it }
    }

    private fun parseError(code: Int, body: String): String {
        val msg = runCatching {
            val e = json.decodeFromString<Err>(body)
            e.msg ?: e.errorDescription ?: e.error
        }.getOrNull()
        return when {
            !msg.isNullOrBlank() -> msg
            code == 400 -> "Invalid email/password or user not registered"
            code == 422 -> "Invalid data — try another email"
            else -> "Login failed ($code)"
        }
    }

    data class Session(val accessToken: String, val email: String, val userId: String)

    @Serializable
    private data class TokenResponse(
        @SerialName("access_token") val accessToken: String = "",
        val user: UserDto? = null,
    )
    @Serializable private data class UserDto(val id: String = "", val email: String? = null)
    @Serializable
    private data class Err(
        val msg: String? = null,
        val error: String? = null,
        @SerialName("error_description") val errorDescription: String? = null,
    )

    private fun String.q() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
