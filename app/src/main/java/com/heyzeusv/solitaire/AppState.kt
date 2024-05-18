package com.heyzeusv.solitaire

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.heyzeusv.solitaire.util.SnackbarManager
import com.heyzeusv.solitaire.util.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class AppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.messages.filterNotNull().collect { snackbarMessage ->
                snackbarHostState.showSnackbar(snackbarMessage.toMessage(resources))
                snackbarManager.clearSnackbarState()
            }
        }
    }
}