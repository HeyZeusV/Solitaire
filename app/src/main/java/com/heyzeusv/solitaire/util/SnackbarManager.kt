package com.heyzeusv.solitaire.util

import android.content.res.Resources
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SnackbarManager {
    private val _messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val messages: StateFlow<SnackbarMessage?> get() = _messages

    fun showMessage(@StringRes message: Int) {
        _messages.value = SnackbarMessage(message)
    }

    fun clearSnackbarState() {
        _messages.value = null
    }
}

data class SnackbarMessage(@StringRes val message: Int) {
    fun toMessage(resources: Resources): String {
        return resources.getString(message)
    }
}