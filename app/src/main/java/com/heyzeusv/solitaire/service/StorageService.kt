package com.heyzeusv.solitaire.service

import com.google.firebase.firestore.FirebaseFirestore
import com.heyzeusv.solitaire.menu.settings.AccountService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {
    suspend fun emailExists(email: String): Boolean {
        val query = firestore.collection(USER_COLLECTION).whereEqualTo("email", email)
        return !query.get().await().isEmpty
    }

    suspend fun addUsername(usernameToAdd: String, email: String) {
        val user = hashMapOf("username" to usernameToAdd, "email" to email)
        val username = hashMapOf("userId" to auth.currentUserId)
        firestore.runBatch { batch ->
            batch.set(firestore.collection(USER_COLLECTION).document(auth.currentUserId), user)
            batch.set(
                firestore.collection(USERNAME_COLLECTION).document(usernameToAdd),
                username
            )
        }.await()
    }

    companion object {
        private const val USER_COLLECTION = "users"
        private const val USERNAME_COLLECTION = "usernames"
    }
}