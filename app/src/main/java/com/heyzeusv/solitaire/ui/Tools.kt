package com.heyzeusv.solitaire.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.ResetOptions.NEW
import com.heyzeusv.solitaire.util.ResetOptions.RESTART
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Composable that displays several buttons for the user. Pressing on Menu button, [menuOnClick],
 *  displays a Composable where user can view games and stats. Pressing on Reset button opens up an
 *  AlertDialog which gives user 3 options, restart current game, reset with a brand new shuffle, or
 *  continue current game; first two options call [resetOnConfirmClick]. Undo button when pressed
 *  calls [undoOnClick], which returns the game back 1 legal move.
 */
@Composable
fun SolitaireTools(
    modifier: Modifier = Modifier,
    menuOnClick: (Boolean) -> Unit,
    resetOnConfirmClick: (ResetOptions) -> Unit,
    updateStats: () -> Unit,
    historyListSize: Int,
    undoOnClick: () -> Unit
) {
    var resetOnClick by remember { mutableStateOf(false) }

    if (resetOnClick) {
        AlertDialog(
            onDismissRequest = { resetOnClick = false },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        resetOnClick = false
                        updateStats()
                        resetOnConfirmClick(RESTART)
                    }) {
                        Text(text = stringResource(R.string.reset_ad_confirm_restart))
                    }
                    TextButton(onClick = {
                        resetOnClick = false
                        updateStats()
                        resetOnConfirmClick(NEW)
                    }) {
                        Text(text = stringResource(R.string.reset_ad_confirm_reset))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { resetOnClick = false }) {
                    Text(text = stringResource(R.string.reset_ad_dismiss))
                }
            },
            title = { Text(text = stringResource(R.string.reset_ad_title)) },
            text = { Text(text = stringResource(R.string.reset_ad_msg)) }
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("Tools"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        val rowModifier = Modifier.weight(1.0f)
        // Menu Button
        SolitaireToolsButton(
            modifier = rowModifier,
            onClick = { menuOnClick(true) },
            iconId = R.drawable.button_menu,
            iconContentDes = stringResource(R.string.tools_cdesc_menu),
            buttonText = stringResource(R.string.tools_button_menu)
        )
        // Reset Button
        SolitaireToolsButton(
            modifier = rowModifier,
            onClick = { resetOnClick = true },
            iconId = R.drawable.button_reset,
            iconContentDes = stringResource(R.string.tools_cdesc_reset),
            buttonText = stringResource(R.string.tools_button_reset)
        )
        // Undo Button
        SolitaireToolsButton(
            modifier = rowModifier,
            enabled = historyListSize > 0,
            onClick = undoOnClick,
            iconId = R.drawable.button_undo,
            iconContentDes = stringResource(R.string.tools_cdesc_undo),
            buttonText = stringResource(R.string.tools_button_undo)
        )
    }
}

/**
 *  All the buttons on [SolitaireTools] are the same Composable but with different
 *  content/functionalities. This Composable only requires the differences. [onClick] is called when
 *  button is pressed. [iconId] refers to the drawable to be display next to [buttonText].
 *  [iconContentDes] is the content description required for the icon.
 */
@Composable
fun SolitaireToolsButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    @DrawableRes iconId: Int,
    iconContentDes: String,
    buttonText: String
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled) Color.White else Color(0x12FFFFFF)
        )
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = iconContentDes,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = buttonText)
    }
}

@Preview
@Composable
fun SolitaireToolsPreview() {
    SolitairePreview {
        SolitaireTools(
            menuOnClick = { },
            resetOnConfirmClick = { },
            updateStats = { },
            historyListSize = 0,
            undoOnClick = { }
        )
    }
}

@Preview
@Composable
fun SolitaireToolsButtonPreview() {
    SolitairePreview {
        Row {
            SolitaireToolsButton(
                onClick = { },
                iconId = R.drawable.button_reset,
                iconContentDes = "",
                buttonText = "Enabled"
            )
            SolitaireToolsButton(
                enabled = false,
                onClick = { },
                iconId = R.drawable.button_reset,
                iconContentDes = "",
                buttonText = "Disabled"
            )
        }
    }
}