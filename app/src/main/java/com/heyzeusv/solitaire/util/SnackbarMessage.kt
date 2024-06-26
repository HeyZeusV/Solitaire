package com.heyzeusv.solitaire.util

import android.content.res.Resources
import androidx.annotation.StringRes
import com.heyzeusv.solitaire.R

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int, val formatArgs: Array<Any?> = emptyArray()) :
        SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resources: Resources): String {
            return when (this) {
                is StringSnackbar -> this.message
                is ResourceSnackbar -> resources.getString(this.message, *this.formatArgs)
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(R.string.generic_error)
        }
    }
}
