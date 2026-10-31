package com.music.pokke.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.music.pokke.PokkeConfig
import java.util.UUID

object GoogleSignInHelper {
    suspend fun getIdToken(context: Context): Result<String> {
        val clientId = PokkeConfig.GOOGLE_WEB_CLIENT_ID
        if (clientId.isBlank()) return Result.failure(IllegalStateException("GOOGLE_WEB_CLIENT_ID vacío"))
        return try {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .setNonce(UUID.randomUUID().toString())
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            val cm = CredentialManager.create(context)
            val result = cm.getCredential(context, request)
            val cred = result.credential
            if (cred is CustomCredential &&
                cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(cred.data)
                Result.success(google.idToken)
            } else {
                Result.failure(IllegalStateException("Credencial Google inesperada"))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
