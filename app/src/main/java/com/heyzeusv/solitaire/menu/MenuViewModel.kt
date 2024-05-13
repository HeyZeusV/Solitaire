package com.heyzeusv.solitaire.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.scoreboard.LastGameStats
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.menu.settings.AccountService
import com.heyzeusv.solitaire.menu.settings.AccountStatus
import com.heyzeusv.solitaire.menu.settings.AccountStatus.*
import com.heyzeusv.solitaire.menu.settings.AccountUiState
import com.heyzeusv.solitaire.menu.stats.StatManager
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.menu.settings.SettingsManager
import com.heyzeusv.solitaire.menu.stats.getStatsDefaultInstance
import com.heyzeusv.solitaire.service.StorageService
import com.heyzeusv.solitaire.util.SnackbarManager
import com.heyzeusv.solitaire.util.SnackbarMessage.Companion.toSnackbarMessage
import com.heyzeusv.solitaire.util.isValidEmail
import com.heyzeusv.solitaire.util.isValidPassword
import com.heyzeusv.solitaire.util.isValidUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
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
    private val accountService: AccountService,
    private val storageService: StorageService
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

    val currentUser = storageService.currentUser

    private val _accountStatus = MutableStateFlow<AccountStatus>(Idle())
    val accountStatus: StateFlow<AccountStatus> get() = _accountStatus

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> get() = _uiState
    fun updateUsername(newValue: String) { _uiState.value = _uiState.value.copy(username = newValue) }
    fun updateEmail(newValue: String) { _uiState.value = _uiState.value.copy(email = newValue) }
    fun updatePassword(newValue: String) { _uiState.value = _uiState.value.copy(password = newValue) }

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

    fun signUpOnClick() {
        uiState.value.let {
            if (!it.username.trim().isValidUsername()) {
                SnackbarManager.showMessage(R.string.username_error)
                return
            }
            if (!it.email.trim().isValidEmail()) {
                SnackbarManager.showMessage(R.string.email_error)
                return
            }
            if (!it.password.isValidPassword()) {
                SnackbarManager.showMessage(R.string.password_error)
                return
            }

            launchCatching {
                _accountStatus.value = UsernameCheck()
                if (storageService.usernameExists(it.username.trim().lowercase())) {
                    SnackbarManager.showMessage(R.string.username_in_use_error)
                } else {
                    _accountStatus.value = CreateAccount()
                    accountService.createAccount(it.email.trim(), it.password)
                    storageService.addUsername(it.username.trim())
                }
            }
        }
    }

    fun logInOnClick() {
        uiState.value.let {
            if (!it.email.isValidEmail()) {
                SnackbarManager.showMessage(R.string.email_error)
                return
            }
            if (it.password.isBlank()) {
                SnackbarManager.showMessage(R.string.empty_password_error)
                return
            }

            launchCatching {
                _accountStatus.value = SignIn()
                accountService.authenticate(it.email, it.password)
            }
        }
    }

    fun signOutOnClick() {
        launchCatching {
            _accountStatus.value = SignOut()
            accountService.signOut()
            _uiState.value = AccountUiState()
        }
    }

    fun forgotPasswordOnClick() {
        uiState.value.let {
            if (!it.email.isValidEmail()) {
                SnackbarManager.showMessage(R.string.email_error)
                return
            }

            launchCatching {
                accountService.sendRecoveryEmail(it.email)
                SnackbarManager.showMessage(R.string.email_password_recovery)
            }
        }
    }

    /**
     *  Attempts to run [block], if exception is caught, displays message as Snackbar.
     */
    private fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                _accountStatus.value = Idle()
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            }
        ) {
            block()
            _accountStatus.value = Idle()
        }
}