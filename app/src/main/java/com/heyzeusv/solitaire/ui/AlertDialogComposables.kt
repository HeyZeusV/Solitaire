package com.heyzeusv.solitaire.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.ui.tools.MenuViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.formatTimeDisplay

@Composable
fun CloseGameAlertDialog(
    sbVM: ScoreboardViewModel,
    menuVM: MenuViewModel,
    finishApp: () -> Unit
) {
    var closeGame by remember { mutableStateOf(false) }

    BackHandler { closeGame = true }
    if (closeGame) {
        AlertDialog(
            onDismissRequest = { closeGame = false },
            confirmButton = {
                TextButton(onClick = {
                    menuVM.checkMovesUpdateStats(sbVM.retrieveLastGameStats(false))
                    finishApp()
                }) {
                    Text(text = stringResource(R.string.close_ad_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { closeGame = false }) {
                    Text(text = stringResource(R.string.close_ad_dismiss))
                }
            },
            title = { Text(text = stringResource(R.string.close_ad_title)) },
            text = { Text(text = stringResource(R.string.close_ad_msg)) }
        )
    }
}

@Composable
fun GameWonAlertDialog(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    menuVM: MenuViewModel
) {
    val gameWon by gameVM.gameWon.collectAsState()

    if (gameWon) {
        // pause timer once user reaches max score
        sbVM.pauseTimer()
        val lgs = sbVM.retrieveLastGameStats(true, gameVM.autoCompleteCorrection)
        menuVM.updateStats(lgs)
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    gameVM.resetAll(ResetOptions.NEW)
                    sbVM.reset()
                }) {
                    Text(text = stringResource(R.string.win_ad_confirm))
                }
            },
            title = { Text(text = stringResource(R.string.win_ad_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.win_ad_msg,
                        lgs.moves,
                        lgs.time.formatTimeDisplay(),
                        lgs.score,
                        lgs.totalScore
                    )
                )
            }
        )
    }
}

@Composable
fun GameSwitchAlertDialog(
    displayGameSwitch: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (displayGameSwitch) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = stringResource(R.string.games_ad_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = stringResource(R.string.games_ad_dismiss))
                }
            },
            title = { Text(text = stringResource(R.string.games_ad_title)) },
            text = { Text(text = stringResource(R.string.games_ad_msg)) }
        )
    }
}

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