package com.heyzeusv.solitaire.ui

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.getContentColor

/**
 *  Custom Button Composable that uses [Box] rather than standard [OutlinedButton] to allow for
 *  easier animations using [Transition]. This custom Button is very bare bones, so it requires
 *  [modifier] to provide additional styling. Displays given [icon] with content description of
 *  [iconContentDesc] and given [buttonText]. [enabled] is used to determine which colors from
 *  [buttonColors] should be used and if [onClick] is ran when Button is clicked.
 */
@Composable
fun BaseButton(
    modifier: Modifier,
    icon: Painter,
    iconContentDesc: String,
    buttonText: String,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = Color.White
    ),
    onClick: () -> Unit
) {
    val contentColor = buttonColors.getContentColor(enabled)

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = contentColor),
            enabled = enabled,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.buttonPaddingHorizontal)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = iconContentDesc,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = contentColor
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = buttonText,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 *  Composable used by [MenuState] screens that display given [menu] name as a title and a back
 *  arrow button that runs [onBackPress] when clicked.
 */
@Composable
fun MenuHeaderBar(
    menu: MenuState,
    onBackPress: () -> Unit
) {
    val menuName = stringResource(menu.nameId)
    Box(modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.button_menu_back),
            contentDescription = stringResource(R.string.menu_cdesc_close, menuName),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = dimensionResource(R.dimen.mhbIconPaddingTop))
                .size(dimensionResource(R.dimen.mhbIconSize))
                .clickable { onBackPress() }
        )
        Text(
            text = menuName,
            modifier = Modifier.align(Alignment.Center),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Preview
@Composable
fun BaseButtonPreview() {
    PreviewUtil().apply {
        Preview {
            Row {
                BaseButton(
                    modifier = Modifier,
                    icon = painterResource(R.drawable.button_reset),
                    iconContentDesc = "",
                    buttonText = "Enabled"
                ) { }
                BaseButton(
                    modifier = Modifier,
                    icon = painterResource(R.drawable.button_reset),
                    iconContentDesc = "",
                    buttonText = "Disabled",
                    enabled = false
                ) { }
            }
        }
    }
}

@Preview
@Composable
fun MenuHeaderBarPreview() {
    PreviewUtil().apply {
        Preview {
            Card {
                Column {
                    MenuHeaderBar(menu = MenuState.Games) { }
                    MenuHeaderBar(menu = MenuState.Stats) { }
                    MenuHeaderBar(menu = MenuState.About) { }
                }
            }
        }
    }
}