package com.heyzeusv.solitaire.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.board.GameViewModel
import com.heyzeusv.solitaire.ui.toolbar.MenuViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.formatTimeDisplay

/**
 *  Basic AlertDialog with a few additional features. [display] determines if AlertDialog should be
 *  displayed and is checked here in order to keep parent Composable a little cleaner.
 *  [runOnDisplay] is called immediately when AlertDialog is shown, without user input. Dismiss
 *  option is completely optional and is not shown if [dismissText] is passed with default value.
 */
@Composable
fun SolitaireAlertDialog(
    display: Boolean,
    title: String,
    message: String,
    confirmText: String,
    confirmOnClick: () -> Unit,
    dismissText: String = "",
    dismissOnClick: () -> Unit = { },
    runOnDisplay: () -> Unit = { }
) {
    if (display) {
        LaunchedEffect(key1 = Unit) {
            runOnDisplay()
        }
        AlertDialog(
            onDismissRequest = dismissOnClick,
            confirmButton = {
                TextButton(onClick = confirmOnClick) {
                    Text(text = confirmText)
                }
            },
            dismissButton = { if (dismissText.isNotBlank()) {
                TextButton(onClick = dismissOnClick) {
                    Text(text = dismissText)
                }
            }},
            title = { Text(text = title) },
            text = { Text(text = message) }
        )
    }
}

/**
 *  AlertDialog for when user tries to close the game with back button and menu closed.
 */
@Composable
fun CloseGameAlertDialog(
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    finishApp: () -> Unit
) {
    var closeGame by remember { mutableStateOf(false) }

    BackHandler { closeGame = true }
    SolitaireAlertDialog(
        display = closeGame,
        title = stringResource(R.string.close_ad_title),
        message = stringResource(R.string.close_ad_msg),
        confirmText = stringResource(R.string.close_ad_confirm),
        confirmOnClick = {
            menuVM.checkMovesUpdateStats(gameVM.sbLogic.retrieveLastGameStats(false))
            finishApp()
        },
        dismissText = stringResource(R.string.close_ad_dismiss),
        dismissOnClick = { closeGame = false }
    )
}

/**
 *  AlertDialog for when user wins the game.
 */
@Composable
fun GameWonAlertDialog(
    gameVM: GameViewModel,
    menuVM: MenuViewModel
) {
    val gameWon by gameVM.gameWon.collectAsState()
    val lgs = gameVM.sbLogic.retrieveLastGameStats(true)

    SolitaireAlertDialog(
        display = gameWon,
        title = stringResource(R.string.win_ad_title),
        message = stringResource(
            R.string.win_ad_msg,
            lgs.moves,
            lgs.time.formatTimeDisplay(),
            lgs.score,
            lgs.totalScore
        ),
        confirmText = stringResource(R.string.win_ad_confirm),
        confirmOnClick = { gameVM.resetAll(ResetOptions.NEW) },
        runOnDisplay = {
            gameVM.sbLogic.pauseTimer()
            menuVM.updateStats(lgs)
        }
    )
}

/**
 *  AlertDialog for when user tries to switch games midway through another.
 */
@Composable
fun GameSwitchAlertDialog(
    displayGameSwitch: Boolean,
    confirmOnClick: () -> Unit,
    dismissOnClick: () -> Unit
) {
    SolitaireAlertDialog(
        display = displayGameSwitch,
        title = stringResource(R.string.games_ad_title),
        message = stringResource(R.string.games_ad_msg),
        confirmText = stringResource(R.string.games_ad_confirm),
        confirmOnClick = confirmOnClick,
        dismissText = stringResource(R.string.games_ad_dismiss),
        dismissOnClick = dismissOnClick
    )
}

/**
 *  AlertDialog for when user presses Reset button on Toolbar, which gives restart, new, or cancel
 *  options. Does not use [SolitaireAlertDialog] since it has 2 confirm buttons.
 */
@Composable
fun ResetAlertDialog(
    displayReset: Boolean,
    updateDisplayReset: (Boolean) -> Unit,
    restartOnConfirm: () -> Unit,
    newOnConfirm: () -> Unit,
) {
    if (displayReset) {
        AlertDialog(
            onDismissRequest = { updateDisplayReset(false) },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        restartOnConfirm()
                        updateDisplayReset(false)
                    }) {
                        Text(text = stringResource(R.string.reset_ad_confirm_restart))
                    }
                    TextButton(onClick = {
                        newOnConfirm()
                        updateDisplayReset(false)
                    }) {
                        Text(text = stringResource(R.string.reset_ad_confirm_new))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { updateDisplayReset(false) }) {
                    Text(text = stringResource(R.string.reset_ad_dismiss))
                }
            },
            title = { Text(text = stringResource(R.string.reset_ad_title)) },
            text = { Text(text = stringResource(R.string.reset_ad_msg)) }
        )
    }
}