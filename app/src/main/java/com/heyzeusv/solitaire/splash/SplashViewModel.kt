package com.heyzeusv.solitaire.splash

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.heyzeusv.solitaire.menu.settings.AccountService
import com.heyzeusv.solitaire.util.isConnected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Data manager for Splash, which tries to create anonymous user.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    private val connectManager: ConnectivityManager
): ViewModel() {
    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> get() = _showError

    /**
     *  Checks to see if a user is signed into [FirebaseAuth], else it calls
     *  [createAnonymousAccount]. Afterwards, navigates to next screen using [navigate]
     */
    fun onAppStart(navigate: () -> Unit) {
        _showError.value = false
        if (connectManager.isConnected()) {
            if (accountService.hasUser) {
                navigate()
            } else {
                createAnonymousAccount(navigate)
            }
        } else {
            navigate()
        }
    }

    /**
     *  Attempts to create an anonymous account in [FirebaseAuth] through [AccountService].
     *  Navigates to next screen using [navigate].
     */
    private fun createAnonymousAccount(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                accountService.createAnonymousAccount()
                navigate()
            } catch (ex: FirebaseAuthException) {
                _showError.value = true
            }
        }
    }
}
