package com.heyzeusv.solitaire.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.util.StatManager
import com.heyzeusv.solitaire.util.Games
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    val stats: StateFlow<StatPreferences> = statManager.statData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatPreferences.getDefaultInstance()
    )


    fun updateStats(lgs: LastGameStats) {
        val gameStatUpdated = stats.value.statsList.find { it.game == _selectedGame.value.dataStoreEnum } ?: GameStats.getDefaultInstance()

        // TODO retrieve initial stat then update it
        gameStatUpdated.newBuilderForType().apply {
            game = _selectedGame.value.dataStoreEnum
            gamesPlayed = gamesPlayed.plus(1)
            gamesWon = gamesWon.plus(if (lgs.gameWon) 1 else 0)
            lowestMoves = if (lgs.gameWon) lowestMoves.coerceAtMost(lgs.moves) else lowestMoves
            totalMoves = totalMoves.plus(lgs.moves)
            fastestWin = if (lgs.gameWon) fastestWin.coerceAtMost(lgs.time) else fastestWin
            totalTime = totalTime.plus(lgs.time)
            totalScore = totalScore.plus(lgs.score)
            bestTotalScore = if (lgs.gameWon) bestTotalScore.coerceAtMost(lgs.totalScore) else bestTotalScore
        }.build()
        val newGameStats = GameStats.newBuilder()
            .setGame(_selectedGame.value.dataStoreEnum)
            .setGamesPlayed(1)
            .setGamesWon(0)
            .setLowestMoves(0)
            .setTotalMoves(lgs.moves)
            .setFastestWin(100)
            .setTotalTime(lgs.time)
            .setTotalScore(lgs.score)
            .setBestTotalScore(1000L)
            .build()
        viewModelScope.launch {
            statManager.updateStats(newGameStats)
        }
    }

    /**
     *  Returns [GameStats] with stats either at 0 or maxed out.
     */
    fun getStatsDefaultInstance(): GameStats {
        return GameStats.getDefaultInstance().toBuilder()
            .setLowestMoves(9999)
            .setFastestWin(359999L)
            .setBestTotalScore(99999L)
            .build()
    }
}