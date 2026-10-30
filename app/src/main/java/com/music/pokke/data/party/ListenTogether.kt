package com.music.pokke.data.party

import com.music.pokke.data.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ListenTogether {
    private val _code = MutableStateFlow<String?>(null)
    val partyCode = _code.asStateFlow()

    fun createParty(): String {
        if (!AppSettings.listenTogether.value) return ""
        val code = (100000..999999).random().toString()
        _code.value = code
        return code
    }

    fun leave() { _code.value = null }
}
