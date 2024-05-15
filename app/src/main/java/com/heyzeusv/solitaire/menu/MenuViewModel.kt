package com.heyzeusv.solitaire.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.scoreboard.LastGameStats
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.service.AccountService
import com.heyzeusv.solitaire.service.AccountStatus
import com.heyzeusv.solitaire.service.AccountStatus.*
import com.heyzeusv.solitaire.menu.settings.AccountUiState
import com.heyzeusv.solitaire.menu.stats.StatManager
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.menu.settings.SettingsManager
import com.heyzeusv.solitaire.menu.stats.getStatsDefaultInstance
import com.heyzeusv.solitaire.service.SingleGameStats
import com.heyzeusv.solitaire.service.StorageService
import com.heyzeusv.solitaire.service.toGameStatsList
import com.heyzeusv.solitaire.service.toSingleGameStatsList
import com.heyzeusv.solitaire.util.SnackbarManager
import com.heyzeusv.solitaire.util.SnackbarMessage.Companion.toSnackbarMessage
import com.heyzeusv.solitaire.util.formatTimeStats
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
import java.util.Date
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
                new.bestCombinedScore =
                    if (lgs.gameWon) {
                        old.bestCombinedScore.coerceAtMost(lgs.totalScore)
                    } else {
                        old.bestCombinedScore
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
                    _accountStatus.value = UploadData()
                    if (stats.value.uid != "") {
                        statManager.deleteAllStats()
                    } else {
                        storageService.uploadGameStats(stats.value.statsList.toSingleGameStatsList())
                    }
                    statManager.updateUID(accountService.currentUserId)
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
                if (stats.value.uid != accountService.currentUserId) {
                    _accountStatus.value = RetrieveData()
                    val fsGameStats = storageService.retrieveGameStats()
                    statManager.addAllStats(fsGameStats.toGameStatsList())
                    statManager.updateUID(accountService.currentUserId)
                }
            }
        }
    }

    fun signOutCheck(): Boolean = stats.value.gameStatsToUploadList.isEmpty()


    fun signOutOnClick() {
        launchCatching {
            _accountStatus.value = SignOut()
            accountService.signOut()
            statManager.deleteAllStats()
            _uiState.value = AccountUiState()
            statManager.updateUID("")
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

    fun uploadStatsOnClick(isConnected: Boolean): Boolean {
        val currentTime = Date()
        return if (!isConnected) {
            SnackbarManager.showMessage(R.string.upload_error_no_internet)
            false
        } else if (!accountService.hasUser) {
            SnackbarManager.showMessage(R.string.upload_error_no_user)
            false
        } else if (currentTime.before(Date(stats.value.nextGameStatsUpload))) {
            val timeLeft = (stats.value.nextGameStatsUpload - currentTime.time) / 1000
            val formattedTimeLeft = timeLeft.formatTimeStats()
            SnackbarManager.showMessage(R.string.upload_error_time, arrayOf(formattedTimeLeft))
            false
        } else if (stats.value.gameStatsToUploadList.isEmpty()) {
            SnackbarManager.showMessage(R.string.upload_error_no_stats)
            false
        } else {
            true
        }
    }

    fun uploadStatsConfirmOnClick() {
        launchCatching {
            _accountStatus.value = UploadData()
            statManager.updateGameStatsUploadTimes()
            val gsToUpload: List<SingleGameStats>
            stats.value.let {
                gsToUpload =
                    it.statsList.filter { gs -> it.gameStatsToUploadList.contains(gs.game) }
                        .toSingleGameStatsList()
            }
            storageService.uploadGameStats(gsToUpload)
            statManager.clearGameStatsToUpload()
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