package com.heyzeusv.solitaire

import android.util.Log
import androidx.datastore.core.DataStore
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
            it.toBuilder().apply {
                ktoGamesPlayed = stats.gamesPlayed
                ktoGamesWon = stats.gamesWon
                ktoLowestMoves = stats.lowestMoves
                ktoAverageMoves = stats.averageMoves
                ktoTotalMoves = stats.totalMoves
                ktoFastestWin = stats.fastestWin
                ktoAverageTime = stats.averageTime
                ktoTotalTime = stats.totalTime
                ktoAverageScore = stats.averageScore
                ktoBestTotalScore = stats.bestTotalScore
            }.build()
        }
    }
}