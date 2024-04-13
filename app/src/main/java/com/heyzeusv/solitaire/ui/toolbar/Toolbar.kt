package com.heyzeusv.solitaire.ui.toolbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.BaseButton
import com.heyzeusv.solitaire.ui.ResetAlertDialog
import com.heyzeusv.solitaire.ui.board.GameViewModel
import com.heyzeusv.solitaire.util.PreviewDevices
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.ResetOptions.NEW
import com.heyzeusv.solitaire.util.ResetOptions.RESTART
import com.heyzeusv.solitaire.util.getContainerColor
import com.heyzeusv.solitaire.util.getContentColor

/**
 *  Compose that displays several tool buttons for the user.
 */
@Composable
fun Toolbar(
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val undoEnabled by gameVM.undoEnabled.collectAsState()
    val autoCompleteActive by gameVM.autoCompleteActive.collectAsState()

    Toolbar(
        menuOnClick = menuVM::updateDisplayMenuButtonsAndMenuState,
        resetRestartOnConfirm = {
            menuVM.checkMovesUpdateStats(gameVM.sbLogic.retrieveLastGameStats(false))
            gameVM.resetAll(RESTART)
        },
        resetNewOnConfirm = {
            menuVM.checkMovesUpdateStats(gameVM.sbLogic.retrieveLastGameStats(false))
            gameVM.resetAll(NEW)
        },
        undoEnabled = undoEnabled,
        undoOnClick = { gameVM.undo() },
        autoCompleteActive = autoCompleteActive,
        modifier = modifier
    )
}

/**
 *  Composable that displays several buttons for the user. Pressing on Menu button, [menuOnClick],
 *  displays a Composable where user can view games and stats. Pressing on Reset button opens up an
 *  AlertDialog which gives user 3 options, restart current game, reset with a brand new shuffle,
 *  or continue current game; first two options call [resetRestartOnConfirm] or [resetNewOnConfirm].
 *  Undo button state is determined by [undoEnabled] and when pressed calls [undoOnClick], which
 *  returns the game back 1 legal move. [autoCompleteActive] determines if Buttons should be
 *  enabled due to game being in autocomplete mode.
 */
@Composable
fun Toolbar(
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
            .padding(horizontal = dimensionResource(R.dimen.tbPaddingHorizontal))
            .padding(bottom = dimensionResource(R.dimen.tbPaddingBottom))
            .testTag("Tools"),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.tbRowSpacedBy)),
        verticalAlignment = Alignment.Top
    ) {
        val rowModifier = Modifier
            .weight(1f)
            .height(dimensionResource(R.dimen.tbButtonHeight))
        // Menu Button
        ToolbarButton(
            modifier = rowModifier,
            icon = painterResource(R.drawable.button_menu),
            iconContentDesc = stringResource(R.string.tools_cdesc_menu),
            buttonText = stringResource(R.string.tools_button_menu),
            enabled = !autoCompleteActive
        ) { menuOnClick() }
        // Reset Button
        ToolbarButton(
            modifier = rowModifier,
            icon = painterResource(R.drawable.button_reset),
            iconContentDesc = stringResource(R.string.tools_cdesc_reset),
            buttonText = stringResource(R.string.tools_button_reset),
            enabled = !autoCompleteActive
        ) {  displayReset = true }
        // Undo Button
        ToolbarButton(
            modifier = rowModifier,
            icon = painterResource(R.drawable.button_undo),
            iconContentDesc = stringResource(R.string.tools_cdesc_undo),
            buttonText = stringResource(R.string.tools_button_undo),
            enabled = undoEnabled && !autoCompleteActive
        ) { undoOnClick() }
    }
}

/**
 *  Wrapper Composable of [BaseButton] to be used by [Toolbar]. Has the same exact
 *  parameters, but has additional [Modifier] to get style needed.
 */
@Composable
fun ToolbarButton(
    modifier: Modifier,
    icon: Painter,
    iconContentDesc: String,
    buttonText: String,
    enabled: Boolean,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = Color.White
    ),
    onClick: () -> Unit
) {
    val containerColor = buttonColors.getContainerColor(enabled)
    val contentColor = buttonColors.getContentColor(enabled)
    val shape = RoundedCornerShape(integerResource(R.integer.buttonRoundPercent))

    BaseButton(
        modifier = modifier
            .border(
                border = BorderStroke(
                    width = dimensionResource(R.dimen.buttonBorderWidth),
                    color = contentColor
                ),
                shape = shape
            )
            .shadow(
                elevation = dimensionResource(R.dimen.buttonElevation),
                shape = shape,
                ambientColor = containerColor,
                spotColor = containerColor
            ),
        iconContentDesc = iconContentDesc,
        buttonText = buttonText,
        iconPainter = icon,
        enabled = enabled
    ) { onClick() }
}

@PreviewDevices
@Preview
@Composable
fun SolitaireToolbarPreview() {
    PreviewUtil().apply {
        Preview {
            Toolbar(
                menuOnClick = { },
                resetRestartOnConfirm = { },
                resetNewOnConfirm = { },
                undoEnabled = true,
                undoOnClick = { },
                autoCompleteActive = false
            )
        }
    }
}

@Preview
@Composable
fun ToolbarButtonPreview() {
    PreviewUtil().apply {
        Preview {
            Row {
                val mod = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.tbButtonHeight))
                ToolbarButton(
                    modifier = mod,
                    icon = painterResource(R.drawable.button_menu),
                    iconContentDesc = "",
                    buttonText = "Enabled",
                    enabled = true
                ) { }
                ToolbarButton(
                    modifier = mod,
                    icon = painterResource(R.drawable.button_undo),
                    iconContentDesc = "",
                    buttonText = "Disabled",
                    enabled = false
                ) { }
            }
        }
    }
}