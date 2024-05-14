package com.heyzeusv.solitaire.service

import com.google.firebase.firestore.DocumentId

data class UserAccount(
    val id: String = "0",
    val isAnonymous: Boolean = true
)

data class UserData(
    @DocumentId val id: String = "0",
    val username: String = ""
)