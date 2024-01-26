package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 *  Composable that displays several buttons for the user. Undo button when pressed calls
 *  [undoOnClick], which returns the game back 1 legal move.
 */
@Composable
fun SolitaireTools(
    modifier: Modifier = Modifier,
    undoOnClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rowModifier = Modifier.weight(1.0f)
        // TODO: Settings or Stats
        SolitaireToolsButton(
            modifier = rowModifier,
            onClick = { },
            iconId = R.drawable.undo,
            iconContentDes = "Open stats screen.",
            buttonText = "Stats"
        )
        // TODO: Reset Button
        SolitaireToolsButton(
            modifier = rowModifier,
            onClick = { },
            iconId = R.drawable.undo,
            iconContentDes = "Reset game.",
            buttonText = "Reset"
        )
        // Undo Button
        SolitaireToolsButton(
            modifier = rowModifier,
            onClick = undoOnClick,
            iconId = R.drawable.undo,
            iconContentDes = "Undo last move.",
            buttonText = "Undo"
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
    onClick: () -> Unit,
    @DrawableRes iconId: Int,
    iconContentDes: String,
    buttonText: String
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White)
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