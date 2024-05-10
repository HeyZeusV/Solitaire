package com.heyzeusv.solitaire.menu

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.scoreboard.LastGameStats
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.menu.stats.StatManager
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.menu.settings.SettingsManager
import com.heyzeusv.solitaire.menu.stats.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.isConnected
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
    private val statManager: StatManager,
    private val connectManager: ConnectivityManager
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

    val stats: StateFlow<StatPreferences> = statManager.statData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = StatPreferences.getDefaultInstance()
    )

    /**
     *  Updates [Settings].[AnimationDurations] using given [animationDurations].
     */
    fun updateAnimationDurations(animationDurations: AnimationDurations) {
        viewModelScope.launch {
            settingsManager.updateAnimationDurations(animationDurations.ads)
        }
    }

    /**
     *  Updates [Settings.selectedGame_] using given [game].
     */
    fun updateSelectedGame(game: Games) {
        viewModelScope.launch {
            settingsManager.updateSelectedGame(game.dataStoreEnum)
        }
    }

    /**
     *  Updates the [GameStats] of [Settings.selectedGame_] using given [lgs].
     */
    fun updateStats(lgs: LastGameStats) {
        val prevGS = stats.value.statsList.find { it.game == settings.value.selectedGame }
            ?: getStatsDefaultInstance()

        var newGS: GameStats
        prevGS.let { old ->
            newGS = GameStats.newBuilder().also { new ->
                new.game = settings.value.selectedGame
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

    /**
     *  Due to scoring change, users before v3.2.0 would have a lower Classic Westcliff average
     *  score. This adds the missing score to total score by multiplying the amount of missing
     *  points (4) by the number of games user played before update. After running, it updates
     *  a boolean in Proto DataStore to ensure it is only ran once per user. Will be removed in the
     *  future.
     */
    fun updateClassicWestcliffScore() {
        if (!settings.value.updatedClassicWestcliffScore) {
            val prevGS = stats.value.statsList.find { it.game == Game.GAME_CLASSIC_WESTCLIFF }
                ?: getStatsDefaultInstance()

            val gamesPlayed = prevGS.gamesPlayed
            val extraScore = gamesPlayed * 4
            val newTotalScore = prevGS.totalScore + extraScore
            val newGS = prevGS.toBuilder()
                .setGame(Game.GAME_CLASSIC_WESTCLIFF)
                .setTotalScore(newTotalScore)
                .build()

            viewModelScope.launch {
                statManager.updateStats(newGS)
                settingsManager.updateUpdatedClassicWestCliffScore()
            }
        }
    }

    fun isConnected(): Boolean = connectManager.isConnected()
}