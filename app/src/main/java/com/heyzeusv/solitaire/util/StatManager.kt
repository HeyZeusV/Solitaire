package com.heyzeusv.solitaire.util

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.data.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatManager @Inject constructor(
    private val statPreferences: DataStore<StatPreferences>
) {

    // preferences will be emitted here
    val statData: Flow<StatPreferences> = statPreferences.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
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
    suspend fun updateStats(stats: Stats) {
        statPreferences.updateData {
            when (stats.gameSelected) {
                Games.KLONDIKETURNONE -> updateKTOStats(it, stats)
                Games.KLONDIKETURNTHREE -> updateKTTStats(it, stats)
            }
        }
    }

    private fun updateKTOStats(sp: StatPreferences, stats: Stats): StatPreferences {
        return sp.toBuilder().apply {
            ktoGamesPlayed = stats.gamesPlayed
            ktoGamesWon = stats.gamesWon
            ktoLowestMoves = stats.lowestMoves
            ktoTotalMoves = stats.totalMoves
            ktoFastestWin = stats.fastestWin
            ktoTotalTime = stats.totalTime
            ktoTotalScore = stats.totalScore
            ktoBestTotalScore = stats.bestTotalScore
        }.build()
    }

    private fun updateKTTStats(sp: StatPreferences, stats: Stats): StatPreferences {
        return sp.toBuilder().apply {
            kttGamesPlayed = stats.gamesPlayed
            kttGamesWon = stats.gamesWon
            kttLowestMoves = stats.lowestMoves
            kttTotalMoves = stats.totalMoves
            kttFastestWin = stats.fastestWin
            kttTotalTime = stats.totalTime
            kttTotalScore = stats.totalScore
            kttBestTotalScore = stats.bestTotalScore
        }.build()
    }
}