package com.heyzeusv.solitaire.ui.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.ResetAlertDialog
import com.heyzeusv.solitaire.ui.SolitaireButton
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.PreviewDevices
import com.heyzeusv.solitaire.util.ResetOptions.NEW
import com.heyzeusv.solitaire.util.ResetOptions.RESTART
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Compose that displays several tool buttons for the user.
 */
@Composable
fun SolitaireTools(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val undoEnabled by gameVM.undoEnabled.collectAsState()
    val autoCompleteActive by gameVM.autoCompleteActive.collectAsState()

    SolitaireTools(
        menuOnClick = menuVM::updateDisplayMenuButtons,
        resetRestartOnConfirm = {
            menuVM.checkMovesUpdateStats(sbVM.retrieveLastGameStats(false))
            gameVM.resetAll(RESTART)
            sbVM.reset()
        },
        resetNewOnConfirm = {
            menuVM.checkMovesUpdateStats(sbVM.retrieveLastGameStats(false))
            gameVM.resetAll(NEW)
            sbVM.reset()
        },
        undoEnabled = undoEnabled,
        undoOnClick = {
            gameVM.undo()
            sbVM.undo()
        },
        autoCompleteActive = autoCompleteActive,
        modifier = modifier
    )
}

/**
 *  Composable that displays several buttons for the user. Pressing on Menu button, [menuOnClick],
 *  displays a Composable where user can view games and stats. Pressing on Reset button opens up an
 *  AlertDialog which gives user 3 options, restart current game, reset with a brand new shuffle, or
 *  continue current game; first two options call [resetRestartOnConfirm] or [resetNewOnConfirm].
 *  Undo button state is determined by [undoEnabled] and when pressed calls [undoOnClick], which
 *  returns the game back 1 legal move. [autoCompleteActive] determines if Buttons should be enabled
 *  due to game being in autocomplete mode.
 */
@Composable
fun SolitaireTools(
    menuOnClick: () -> Unit,
    resetRestartOnConfirm: () -> Unit,
    resetNewOnConfirm: () -> Unit,
    undoEnabled: Boolean,
    undoOnClick: () -> Unit,
    autoCompleteActive: Boolean,
    modifier: Modifier = Modifier,
) {
    var displayReset by remember { mutableStateOf(false) }

    ResetAlertDialog(
        displayReset = displayReset,
        updateDisplayReset = { displayReset = it },
        restartOnConfirm = resetRestartOnConfirm,
        newOnConfirm = resetNewOnConfirm
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 24.dp)
            .testTag("Tools"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        val rowModifier = Modifier
            .weight(1f)
            .height(48.dp)
        // Menu Button
        SolitaireButton(
            onClick = menuOnClick,
            icon = painterResource(R.drawable.button_menu),
            iconContentDes = stringResource(R.string.tools_cdesc_menu),
            buttonText = stringResource(R.string.tools_button_menu),
            modifier = rowModifier,
            enabled = !autoCompleteActive
        )
        // Reset Button
        SolitaireButton(
            onClick = { displayReset = true },
            icon = painterResource(R.drawable.button_reset),
            iconContentDes = stringResource(R.string.tools_cdesc_reset),
            buttonText = stringResource(R.string.tools_button_reset),
            modifier = rowModifier,
            enabled = !autoCompleteActive
        )
        // Undo Button
        SolitaireButton(
            onClick = undoOnClick,
            icon = painterResource(R.drawable.button_undo),
            iconContentDes = stringResource(R.string.tools_cdesc_undo),
            buttonText = stringResource(R.string.tools_button_undo),
            modifier = rowModifier,
            enabled = undoEnabled && !autoCompleteActive
        )
    }
}

@PreviewDevices
@Preview
@Composable
fun SolitaireToolsPreview() {
    SolitairePreview {
        SolitaireTools(
            menuOnClick = { },
            resetRestartOnConfirm = { },
            resetNewOnConfirm = { },
            undoEnabled = true,
            undoOnClick = { },
            autoCompleteActive = false
        )
    }
}