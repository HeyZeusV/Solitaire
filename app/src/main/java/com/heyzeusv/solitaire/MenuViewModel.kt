package com.heyzeusv.solitaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val statManager: StatManager
) : ViewModel() {

    private val _displayMenu = MutableStateFlow(false)
    val displayMenu: StateFlow<Boolean> get() = _displayMenu
    fun updateDisplayMenu(newValue: Boolean) { _displayMenu.value = newValue }

    private val _selectedGame = MutableStateFlow(Games.KLONDIKETURNONE)
    val selectedGame: StateFlow<Games> get() = _selectedGame
    fun updateSelectedGame(newValue: Games) { _selectedGame.value = newValue }

    val stats: StateFlow<Stats> = statManager.statData
        .map {
            Stats(
                gamesPlayed = it.ktoGamesPlayed,
                gamesWon = it.ktoGamesWon,
                lowestMoves = it.ktoLowestMoves,
                averageMoves = it.ktoAverageMoves,
                totalMoves = it.ktoTotalMoves,
                fastestWin = it.ktoFastestWin,
                averageTime = it.ktoAverageTime,
                totalTime = it.ktoTotalTime,
                averageScore = it.ktoAverageScore,
                bestTotalScore = it.ktoBestTotalScore
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Stats()
        )

    fun updateStats(lgs: LastGameStats) {
        viewModelScope.launch {
            var updatedStats: Stats
            stats.value.run {
                updatedStats = copy(
                    gamesPlayed = gamesPlayed.plus(1),
                    gamesWon = gamesWon.plus(if (lgs.gameWon) 1 else 0),
                    lowestMoves = if (lgs.gameWon) lowestMoves.coerceAtMost(lgs.moves) else lowestMoves,
                    averageMoves = averageInt(lgs.moves, totalMoves, gamesPlayed),
                    totalMoves = totalMoves.plus(lgs.moves),
                    fastestWin = if (lgs.gameWon) fastestWin.coerceAtMost(lgs.time) else fastestWin,
                    averageTime = averageLong(lgs.time, totalTime, gamesPlayed),
                    totalTime = totalTime.plus(lgs.time),
                    averageScore = averageInt(lgs.score, (averageScore * gamesPlayed), gamesPlayed),
                    bestTotalScore = if (lgs.gameWon) bestTotalScore.coerceAtMost(lgs.totalScore) else bestTotalScore
                )
            }
            statManager.updateStats(updatedStats)
        }
    }

    private fun averageLong(newStat: Long, previousTotal: Long, previousGamesPlayed: Int): Long {
        return (newStat + previousTotal) / (previousGamesPlayed + 1)
    }

    private fun averageInt(newStat: Int, previousTotal: Int, previousGamesPlayed: Int): Int {
        return (newStat + previousTotal) / (previousGamesPlayed + 1)
    }
}