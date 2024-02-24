package com.heyzeusv.solitaire.ui.tools

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.ui.SolitaireButton
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Composable that displays Menu options which transition into Menu screens.
 */
@Composable
fun MenuContainer(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val displayMenuButtons by menuVM.displayMenuButtons.collectAsState()
    val menuState by menuVM.menuState.collectAsState()
    Column(modifier = modifier) {
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.GAMES,
            content = { StatsScreen(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM) }
        )
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.STATS,
            content = { StatsScreen(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM) }
        )
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.ABOUT,
            transformOrigin = TransformOrigin(0.5f, 0.20f),
            content = { StatsScreen(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM) },
            bottomPadding = 80.dp
        )
    }
}

/**
 *  Composable that transitions from given [option] Button to [content] Screen. [displayMenuButtons]
 *  determines if initial Button should be animated in/out. [menuState] determines which Composable
 *  to Transition to, while [updateMenuState] changes that value. [bottomPadding] is needed due to
 *  not being anchored to another Composable, as well as having to transition to full screen.
 *  [transformOrigin] determines where Button scales in/out from.
 */
@Composable
fun MenuOptionTransition(
    displayMenuButtons: Boolean,
    menuState: MenuState,
    updateMenuState: (MenuState) -> Unit,
    option: MenuState,
    content: @Composable () -> Unit,
    bottomPadding: Dp = 8.dp,
    transformOrigin: TransformOrigin = TransformOrigin.Center
) {

    val transition = updateTransition(targetState = menuState, label = "Menu Transition")
    val backgroundColor by transition.animateColor(
        label = "Menu ${option.name} backgroundColor Transition"
    ) { state ->
        when (state) {
            option -> MaterialTheme.colorScheme.surfaceVariant
            else -> Color.Transparent
        }
    }
    val cornerRadius by transition.animateDp(
        label = "Menu ${option.name} cornerRadius Transition",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 250,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = 250,
                    easing = EaseInCubic
                )
            }
        }
    ) { state ->
        when (state) {
            option -> 0.dp
            else -> 20.dp
        }
    }
    val borderWidth by transition.animateDp(
        label = "Menu ${option.name} borderWidth Transition",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 250,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = 250,
                    easing = EaseInCubic
                )
            }
        }
    ) { state ->
        when (state) {
            option -> 0.dp
            else -> 2.dp
        }
    }
    val borderColor by transition.animateColor(
        label = "Menu ${option.name} borderColor Transition",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 250,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = 250,
                    easing = EaseInCubic
                )
            }
        }
    ) { state ->
        when (state) {
            option -> Color.Transparent
            else -> Color.White
        }
    }
    val elevation by transition.animateDp(
        label = "Menu ${option.name} elevation Transition",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 250,
                    easing = EaseOutCubic,
                )else -> tween(
                    durationMillis = 250,
                    easing = EaseInCubic,
                )
            }
        }
    ) { state ->
        when (state) {
            option -> 0.dp
            else -> 1.dp
        }
    }
    val paddingStart by transition.animateDp(
        label = "Menu ${option.name} paddingStart Transition"
    ) { state ->
        when (state) {
            option -> 0.dp
            else -> 12.dp
        }
    }
    val paddingBottom by transition.animateDp(
        label = "Menu ${option.name} paddingBottom Transition"
    ) { state ->
        when (state) {
            MenuState.BUTTONS -> bottomPadding
            else -> 0.dp
        }
    }

    AnimatedVisibility(
        visible = displayMenuButtons,
        enter = scaleIn(transformOrigin = transformOrigin),
        exit = scaleOut(transformOrigin = transformOrigin)
    ) {
        transition.AnimatedContent(
            modifier = Modifier
                .padding(start = paddingStart, bottom = paddingBottom)
                .shadow(
                    elevation = elevation, shape = RoundedCornerShape(cornerRadius),
                    ambientColor = Color.Transparent,
                    spotColor = Color.Transparent
                )
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = borderColor
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .drawBehind { drawRect(backgroundColor) },
            transitionSpec = {
                (fadeIn(animationSpec = tween(durationMillis = 250, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(durationMillis = 250)))
                    .using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ ->
                        tween(durationMillis = 250, easing = FastOutSlowInEasing)
                    }))
            }
        ) { state ->
            when (state) {
                option -> content()
                MenuState.BUTTONS -> MenuOptionButton(
                    option = option,
                    onClick = { updateMenuState(option) }
                )
                else -> {}
            }
        }
    }
}

/**
 *  Copy of [SolitaireButton] Composable built from Box rather than Button in order for
 *  [AnimatedContent] to work correctly. [option] contains the text/icon data to be displayed.
 *  [onClick] is ran when pressed.
 */
@Composable
fun MenuOptionButton(
    option: MenuState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(0.3f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(option.iconId),
                contentDescription = stringResource(option.iconDescId),
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(option.nameId),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(name = "NEXUS_7", device = Devices.NEXUS_7)
@Preview(name = "NEXUS_7_2013", device = Devices.NEXUS_7_2013)
@Preview(name = "NEXUS_5", device = Devices.NEXUS_5) // 2013
@Preview(name = "NEXUS_6", device = Devices.NEXUS_6)
@Preview(name = "NEXUS_9", device = Devices.NEXUS_9)
@Preview(name = "NEXUS_10", device = Devices.NEXUS_10)
@Preview(name = "NEXUS_5X", device = Devices.NEXUS_5X)
@Preview(name = "NEXUS_6P", device = Devices.NEXUS_6P)
@Preview(name = "PIXEL_C", device = Devices.PIXEL_C)
@Preview(name = "PIXEL", device = Devices.PIXEL)
@Preview(name = "PIXEL_XL", device = Devices.PIXEL_XL)
@Preview(name = "PIXEL_2", device = Devices.PIXEL_2)
@Preview(name = "PIXEL_2_XL", device = Devices.PIXEL_2_XL)
@Preview(name = "PIXEL_3", device = Devices.PIXEL_3)
@Preview(name = "PIXEL_3_XL", device = Devices.PIXEL_3_XL)
@Preview(name = "PIXEL_3A", device = Devices.PIXEL_3A)
@Preview(name = "PIXEL_3A_XL", device = Devices.PIXEL_3A_XL)
@Preview(name = "PIXEL_4", device = Devices.PIXEL_4)
@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL)
@Preview
@Composable
fun MenuOptionButtonPreview() {
    SolitairePreview {
        Box(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()) {
            MenuOptionButton(
                option = MenuState.STATS,
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .background(Color.Gray)
            )
        }
    }
}