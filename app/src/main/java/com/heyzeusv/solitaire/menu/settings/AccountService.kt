package com.heyzeusv.solitaire.menu.settings

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 *  Connects to [FirebaseAuth] to handle User Accounts.
 *  Found on [Make-It-So Repo](https://github.com/FirebaseExtended/make-it-so-android/blob/main/final/app/src/main/java/com/example/makeitso/model/service/impl/AccountServiceImpl.kt).
 */
class AccountService @Inject constructor(private val auth: FirebaseAuth) {

    val hasUser: Boolean = auth.currentUser != null

    val currentUser: Flow<User> = callbackFlow {
        val listener =
            FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let { User(it.uid, it.displayName, it.isAnonymous) } ?: User())
            }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }

    suspend fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun updateDisplayName(username: String) {
        auth.currentUser!!.updateProfile(userProfileChangeRequest {
            displayName = username
        }).await()
    }

    suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()

        // Sign the user back in anonymously.
        createAnonymousAccount()
    }
}

data class User(
    val id: String = "",
    val displayName: String? = "",
    val isAnonymous: Boolean = true
)