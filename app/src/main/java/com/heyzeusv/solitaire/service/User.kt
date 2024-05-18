package com.heyzeusv.solitaire.service

import androidx.annotation.Keep

@Keep
data class UserAccount(
    val id: String = "0",
    val displayName: String = "",
    val isAnonymous: Boolean = true
)