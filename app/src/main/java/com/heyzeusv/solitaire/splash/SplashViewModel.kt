package com.heyzeusv.solitaire.splash

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.heyzeusv.solitaire.menu.settings.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
     *  Checks to see if a user is signed into [FirebaseAuth]. Afterwards, navigates to next screen
     *  using [navigate].
     */
    fun onAppStart(navigate: () -> Unit) {
        _showError.value = false
        if (accountService.hasUser) {
            Log.d("tag", "has user")
        }
        navigate()
    }
}
