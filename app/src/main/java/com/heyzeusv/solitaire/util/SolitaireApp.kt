package com.heyzeusv.solitaire.util

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.heyzeusv.solitaire.BuildConfig
import dagger.hilt.android.HiltAndroidApp

/**
 *  Maintains global application state and used to call one-time operations.
 */
@HiltAndroidApp
class SolitaireApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(context = this)
        if (BuildConfig.DEBUG) {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            Firebase.appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}