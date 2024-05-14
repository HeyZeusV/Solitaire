package com.heyzeusv.solitaire.service

import com.google.firebase.auth.FirebaseAuth
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

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val currentUser: Flow<UserAccount>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { UserAccount(it.uid, it.isAnonymous) } ?: UserAccount())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}

data class UserAccount(
    val id: String = "0",
    val isAnonymous: Boolean = true
)

data class UserData(
    val id: String = "0",
    val username: String = ""
)