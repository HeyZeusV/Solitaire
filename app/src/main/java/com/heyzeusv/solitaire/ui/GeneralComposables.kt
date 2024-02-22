package com.heyzeusv.solitaire.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  [onClick] is called when button is pressed. [icon] refers to the drawable to be display next to
 *  [buttonText]. [iconContentDes] is the content description required for the icon. [enabled]
 *  determines if user is able to interact with Button.
 */
@Composable
fun SolitaireButton(
    onClick: () -> Unit,
    icon: Painter,
    iconContentDes: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
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
            painter = icon,
            contentDescription = iconContentDes,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = buttonText)
    }
}

@Preview
@Composable
fun SolitaireButtonPreview() {
    SolitairePreview {
        Row {
            SolitaireButton(
                onClick = { },
                icon = painterResource(R.drawable.button_reset),
                iconContentDes = "",
                buttonText = "Enabled"
            )
            SolitaireButton(
                onClick = { },
                icon = painterResource(R.drawable.button_reset),
                iconContentDes = "",
                buttonText = "Disabled",
                enabled = false
            )
        }
    }
}