package com.heyzeusv.solitaire.menu.stats

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.util.inFiveMinutes
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
    suspend fun updateStats(localStats: GameStats) {
        statPreferences.updateData { statPrefs ->
            val index = statPrefs.statsList.indexOfFirst { it.game == localStats.game }
            val builder = statPrefs.toBuilder()
            if (index == -1) {
                builder.addStats(localStats)
            } else {
                builder.setStats(index, localStats)
            }

            builder.build()
        }
    }

    /**
     *  Adds all given [stats]. Called when user logs in.
     */
    suspend fun addAllPersonalStats(stats: List<GameStats>) {
        statPreferences.updateData { statPrefs ->
            statPrefs.toBuilder().clearStats().addAllStats(stats).build()
        }
    }

    suspend fun addAllGlobalStats(stats: List<GameStats>) {
        statPreferences.updateData { statPrefs ->
            statPrefs.toBuilder().clearGlobalStats().addAllGlobalStats(stats).build()
        }
    }

    /**
     *  Delete all stored stats. Called when user logs out.
     */
    suspend fun deleteAllPersonalStats() {
        statPreferences.updateData { it.toBuilder().setStatsToUpload(false).clearStats().build() }
    }

    /**
     *  Updates next_game_stats_upload to 5 minutes past time this function is called.
     */
    suspend fun updateGameStatsSyncTime() {
        statPreferences.updateData { statPrefs ->
            val date = Date()
            statPrefs.toBuilder().setNextGameStatsSync(date.inFiveMinutes()).build()
        }
    }

    suspend fun setStatsToUpload(newValue: Boolean) {
        statPreferences.updateData { it.toBuilder().setStatsToUpload(newValue).build() }
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