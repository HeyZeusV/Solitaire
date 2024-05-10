package com.heyzeusv.solitaire.util

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SnackbarManager {
    private val _messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val messages: StateFlow<SnackbarMessage?> get() = _messages

    fun showMessage(@StringRes message: Int) {
        _messages.value = SnackbarMessage.ResourceSnackbar(message)
    }

    fun showMessage(message: SnackbarMessage) {
        _messages.value = message
    }

    fun clearSnackbarState() {
        _messages.value = null
    }
}