package com.heyzeusv.solitaire.service

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId

@Keep
data class UserAccount(
    val id: String = "0",
    val isAnonymous: Boolean = true
)

@Keep
data class UserData(
    @DocumentId val id: String = "0",
    val username: String = ""
)