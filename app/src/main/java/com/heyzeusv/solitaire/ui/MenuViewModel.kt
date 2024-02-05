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
        val prevGS =
            stats.value.statsList.find { it.game == _selectedGame.value.dataStoreEnum }
                ?: getStatsDefaultInstance()

        var newGS: GameStats
        prevGS.let { old ->
            newGS = GameStats.newBuilder().also { new ->
                new.game = _selectedGame.value.dataStoreEnum
                new.gamesPlayed = old.gamesPlayed.plus(1)
                new.gamesWon = old.gamesWon.plus(if (lgs.gameWon) 1 else 0)
                new.lowestMoves =
                    if (lgs.gameWon) old.lowestMoves.coerceAtMost(lgs.moves) else old.lowestMoves
                new.totalMoves = old.totalMoves.plus(lgs.moves)
                new.fastestWin =
                    if (lgs.gameWon) old.fastestWin.coerceAtMost(lgs.time) else old.fastestWin
                new.totalTime = old.totalTime.plus(lgs.time)
                new.totalScore = old.totalScore.plus(lgs.score)
                new.bestTotalScore =
                    if (lgs.gameWon) old.bestTotalScore.coerceAtMost(lgs.totalScore) else old.bestTotalScore
            }.build()
        }
        viewModelScope.launch {
            statManager.updateStats(newGS)
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