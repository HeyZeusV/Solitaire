package com.heyzeusv.solitaire.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.Stats
import com.heyzeusv.solitaire.util.StatManager
import com.heyzeusv.solitaire.util.Games
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Data manager for menu.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
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

    val stats: StateFlow<Stats> = statManager.statData.map {
        Stats(
            gamesPlayed = it.ktoGamesPlayed,
            gamesWon = it.ktoGamesWon,
            lowestMoves = it.ktoLowestMoves,
            totalMoves = it.ktoTotalMoves,
            fastestWin = it.ktoFastestWin,
            totalTime = it.ktoTotalTime,
            totalScore = it.ktoTotalScore,
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
                    totalMoves = totalMoves.plus(lgs.moves),
                    fastestWin = if (lgs.gameWon) fastestWin.coerceAtMost(lgs.time) else fastestWin,
                    totalTime = totalTime.plus(lgs.time),
                    totalScore = totalScore.plus(lgs.score),
                    bestTotalScore = if (lgs.gameWon) bestTotalScore.coerceAtMost(lgs.totalScore) else bestTotalScore
                )
            }
            statManager.updateStats(updatedStats)
        }
    }
}