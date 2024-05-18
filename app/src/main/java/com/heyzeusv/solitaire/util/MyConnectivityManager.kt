package com.heyzeusv.solitaire.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

/**
 *  Used to keep track of device connection to internet when observing [connectionAsStateFlow].
 *  Found (here)[https://medium.com/@meytataliti/obtaining-network-connection-info-with-flow-in-android-af2e6b760dfd]
 */
class MyConnectivityManager(context: Context, private val externalScope: CoroutineScope) {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    val connectionAsStateFlow: StateFlow<Boolean>
        get() = _connectionFlow
            .stateIn(
                scope = externalScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = isConnected
            )

    private val _connectionFlow = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network : Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                if (networkCapabilities.isNetworkCapabilitiesValid()) {
                    trySend(true)
                }
            }
        }
        subscribe(networkCallback)
        awaitClose {
            unsubscribe(networkCallback)
        }
    }

    private val isConnected: Boolean
        get() {
            val activeNetwork = connectivityManager.activeNetwork
            return if (activeNetwork == null) {
                false
            } else {
                val netCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                netCapabilities.isNetworkCapabilitiesValid()
            }
        }

    private fun subscribe(networkCallback: ConnectivityManager.NetworkCallback) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unsubscribe(networkCallback: ConnectivityManager.NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}