package com.heyzeusv.solitaire.util

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.solitaire.AnimationDurationsSetting
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val settings: DataStore<Settings>
) {
    // settings will be emitted here
    val settingsData: Flow<Settings> = settings.data
        .catch { exception ->
            // DataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Solitaire", "Error reading settings.", exception)
                emit(
                    Settings.getDefaultInstance().toBuilder()
                        .setAnimationDurations(AnimationDurationsSetting.FAST)
                        .setSelectedGame(Game.GAME_KLONDIKETURNONE)
                        .build()
                )
            } else {
                throw exception
            }
        }

    suspend fun updateAnimationDurations(animationDurations: AnimationDurationsSetting) {
        settings.updateData { it.toBuilder().setAnimationDurations(animationDurations).build() }
    }

    suspend fun updateSelectedGame(game: Game) {
        settings.updateData { it.toBuilder().setSelectedGame(game).build() }
    }

    suspend fun updateUpdatedClassicWestCliffScore() {
        settings.updateData { it.toBuilder().setUpdatedClassicWestcliffScore(true).build() }
    }
}
