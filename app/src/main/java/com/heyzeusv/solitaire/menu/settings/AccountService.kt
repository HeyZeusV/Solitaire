package com.heyzeusv.solitaire.menu.settings

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 *  Connects to [FirebaseAuth] to handle User Accounts.
 *  Found on [Make-It-So Repo](https://github.com/FirebaseExtended/make-it-so-android/blob/main/final/app/src/main/java/com/example/makeitso/model/service/impl/AccountServiceImpl.kt).
 */
class AccountService @Inject constructor(private val auth: FirebaseAuth) {

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val hasUser: Boolean
        get() = auth.currentUser != null

    val isAnonymous: Boolean
        get() = auth.currentUser!!.isAnonymous

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> get() = _currentUser

    suspend fun authenticate(email: String, password: String) {
        auth.currentUser?.let {
            if (it.isAnonymous) {
                it.delete()
                auth.signOut()
            }
        }
        auth.signInWithEmailAndPassword(email, password).await()
        _currentUser.value =
            auth.currentUser?.let { User(it.uid, it.displayName, it.isAnonymous) } ?: User()
    }

    suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }

    suspend fun recreateAnonymousAccount() {
        auth.currentUser?.let {
            it.delete()
            auth.signOut()
            auth.signInAnonymously().await()
        }
    }

    suspend fun createAccount(email: String, password: String) {
        auth.currentUser?.let {
            if (it.isAnonymous) {
                it.delete()
                auth.signOut()
            }
        }
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun updateDisplayName(username: String) {
        auth.currentUser!!.updateProfile(userProfileChangeRequest {
            displayName = username
        }).await()
        _currentUser.value =
            auth.currentUser?.let { User(it.uid, it.displayName, it.isAnonymous) } ?: User()
    }

    suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()
        _currentUser.value = User()
        // Sign the user back in anonymously.
        createAnonymousAccount()
    }

    suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}

data class User(
    val id: String = "",
    val displayName: String? = "",
    val isAnonymous: Boolean = true
)