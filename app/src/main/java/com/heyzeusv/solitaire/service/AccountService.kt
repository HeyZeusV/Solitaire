package com.heyzeusv.solitaire.service

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

    private val _userAccount = MutableStateFlow<UserAccount?>(null)
    val userAccount: StateFlow<UserAccount?> get() = _userAccount

    suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        auth.currentUser?.let {
            _userAccount.value = UserAccount(it.uid, it.displayName!!, it.isAnonymous)
        }
    }

    suspend fun createAccount(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser?.let {
            it.updateProfile(userProfileChangeRequest { displayName = name }).await()
            _userAccount.value = UserAccount(it.uid, it.displayName!!, it.isAnonymous)
        }

    }

    fun signOut() {
        auth.signOut()
        _userAccount.value = null
    }

    suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    init {
        auth.currentUser?.let {
            _userAccount.value = UserAccount(it.uid, it.displayName!!, it.isAnonymous)
        }
    }
}