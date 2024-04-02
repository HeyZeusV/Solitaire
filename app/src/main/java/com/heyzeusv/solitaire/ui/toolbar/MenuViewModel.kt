package com.heyzeusv.solitaire.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.StatManager
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SettingsManager
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
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
    private val settingsManager: SettingsManager,
    private val statManager: StatManager
) : ViewModel() {

    private val _displayMenuButtons = MutableStateFlow(false)
    val displayMenuButtons: StateFlow<Boolean> get() = _displayMenuButtons
    private fun updateDisplayMenuButtons() { _displayMenuButtons.value = !_displayMenuButtons.value }

    private val _menuState = MutableStateFlow(MenuState.Buttons)
    val menuState: StateFlow<MenuState> get() = _menuState
    fun updateMenuState(newValue: MenuState) { _menuState.value = newValue}
    fun updateDisplayMenuButtonsAndMenuState(newMenuState: MenuState = MenuState.Buttons) {
        updateDisplayMenuButtons()
        updateMenuState(newMenuState)
    }

    val settings: StateFlow<Settings> = settingsManager.settingsData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Settings.getDefaultInstance()
    )

    private val _selectedGame = MutableStateFlow(Games.KLONDIKE_TURN_ONE)
    val selectedGame: StateFlow<Games> get() = _selectedGame
    fun updateSelectedGame(newValue: Games) {
        _selectedGame.value = newValue
        _statsSelectedGame.value = newValue
    }

    private val _statsSelectedGame = MutableStateFlow(Games.KLONDIKE_TURN_ONE)
    val statsSelectedGame: StateFlow<Games> get() = _statsSelectedGame
    fun updateStatsSelectedGame(newValue: Games) { _statsSelectedGame.value = newValue }

    val stats: StateFlow<StatPreferences> = statManager.statData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = StatPreferences.getDefaultInstance()
    )

    fun updateAnimationDurations(animationDurations: AnimationDurations) {
        viewModelScope.launch {
            settingsManager.updateAnimationDurations(animationDurations.ads)
        }
    }

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
                    if (lgs.gameWon) {
                        old.bestTotalScore.coerceAtMost(lgs.totalScore)
                    } else {
                        old.bestTotalScore
                    }
            }.build()
        }
        viewModelScope.launch {
            statManager.updateStats(newGS)
        }
    }

    /**
     *  Checks the number of moves in given [lgs] to confirm if call to [updateStats] is needed.
     */
    fun checkMovesUpdateStats(lgs: LastGameStats) {
        if (lgs.moves > 1) {
            updateStats(lgs)
        }
    }
}