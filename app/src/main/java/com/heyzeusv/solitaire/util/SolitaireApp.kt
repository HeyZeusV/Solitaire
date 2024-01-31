package com.heyzeusv.solitaire.util

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 *  Maintains global application state and used to call one-time operations.
 */
@HiltAndroidApp
class SolitaireApp : Application()