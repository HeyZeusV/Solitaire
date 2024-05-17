package com.heyzeusv.solitaire.menu.stats

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.util.endOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatManager @Inject constructor(
    private val statPreferences: DataStore<StatPreferences>
) {
    // preferences will be emitted here
    val statData: Flow<StatPreferences> = statPreferences.data
        .catch { exception ->
            // DataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Solitaire", "Error reading stats.", exception)
                emit(StatPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     *  Stats can all be updated together when a game is ended by any means.
     */
    suspend fun updateStats(stats: GameStats, signedIn: Boolean) {
        statPreferences.updateData { statPrefs ->
            val index = statPrefs.statsList.indexOfFirst { it.game == stats.game }
            val builder = statPrefs.toBuilder()
            if (index == -1) {
                builder.addStats(stats).build()
            } else {
                builder.setStats(index, stats)
            }
            if (signedIn && !statPrefs.gameStatsToUploadList.contains(stats.game)) {
                builder.addGameStatsToUpload(stats.game)
            }
            builder.build()
        }
    }

    /**
     *  Adds all given [stats]. Called when user logs in.
     */
    suspend fun addAllStats(stats: List<GameStats>) {
        statPreferences.updateData { statPrefs ->
            statPrefs.toBuilder().clearStats().addAllStats(stats).build()
        }
    }

    /**
     *  Delete all stored stats. Called when user logs out.
     */
    suspend fun deleteAllStats() {
        statPreferences.updateData { it.toBuilder().clearGameStatsToUpload().clearStats().build() }
    }

    /**
     *  Updates last_game_stats_upload to current time and next_game_stats_upload to end of day.
     */
    suspend fun updateGameStatsUploadTimes() {
        statPreferences.updateData { statPrefs ->
            val date = Date()
            statPrefs.toBuilder()
                .setLastGameStatsUpload(date.time)
                .setNextGameStatsUpload(date.endOfDay())
                .build()
        }
    }

    suspend fun clearGameStatsToUpload() {
        statPreferences.updateData { statPrefs ->
            statPrefs.toBuilder().clearGameStatsToUpload().build()
        }
    }

    suspend fun updateUID(uid: String) {
        statPreferences.updateData { statPrefs ->
            statPrefs.toBuilder().setUid(uid).build()
        }
    }
}

/**
 *  Returns [GameStats] tied to given [game] with stats either at 0 or maxed out.
 */
fun getStatsDefaultInstance(game: Game): GameStats {
    return GameStats.getDefaultInstance().toBuilder()
        .setGame(game)
        .setLowestMoves(9999)
        .setFastestWin(359999L)
        .setBestCombinedScore(99999L)
        .build()
}