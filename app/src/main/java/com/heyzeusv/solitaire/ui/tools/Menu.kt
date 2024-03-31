package com.heyzeusv.solitaire.ui.tools

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.SolitaireButton
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewDevices
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
            option = MenuState.Games,
            content = { GamesMenu(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM) }
        )
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.Stats,
            content = { StatsMenu(menuVM = menuVM) }
        )
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.About,
            transformOrigin = TransformOrigin(0.5f, 0.20f),
            content = {
                AboutMenu {
                    menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
                }
            },
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
    val menuButtonDuration = integerResource(R.integer.menuButtonDuration)
    val menuButtonDelay = integerResource(R.integer.menuButtonDelay)
    val menuButtonAniSpec = if (menuState == MenuState.ButtonsFromScreen) {
        tween<Float>(menuButtonDuration, menuButtonDelay)
    } else {
        tween(menuButtonDuration)
    }
    val menuOptionDuration = integerResource(R.integer.menuOptionDuration)

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
                MenuState.Buttons, MenuState.ButtonsFromScreen -> tween(
                    durationMillis = menuOptionDuration,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = menuOptionDuration,
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
                MenuState.Buttons, MenuState.ButtonsFromScreen -> tween(
                    durationMillis = menuOptionDuration,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = menuOptionDuration,
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
                MenuState.Buttons, MenuState.ButtonsFromScreen -> tween(
                    durationMillis = menuOptionDuration,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = menuOptionDuration,
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
                MenuState.Buttons, MenuState.ButtonsFromScreen -> tween(
                    durationMillis = menuOptionDuration,
                    easing = EaseOutCubic,
                )else -> tween(
                    durationMillis = menuOptionDuration,
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
            MenuState.Buttons, MenuState.ButtonsFromScreen -> bottomPadding
            else -> 0.dp
        }
    }

    AnimatedVisibility(
        visible = displayMenuButtons,
        enter = scaleIn(
            animationSpec = menuButtonAniSpec,
            transformOrigin = transformOrigin
        ),
        exit = scaleOut(
            animationSpec = menuButtonAniSpec,
            transformOrigin = transformOrigin
        )
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
                (fadeIn(animationSpec = tween(durationMillis = menuOptionDuration)))
                    .togetherWith(fadeOut(tween(durationMillis = menuOptionDuration)))
                    .using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ ->
                        tween(durationMillis = menuOptionDuration)
                    }))
            }
        ) { state ->
            when (state) {
                option -> content()
                MenuState.Buttons, MenuState.ButtonsFromScreen -> MenuOptionButton(
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            ),
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

@PreviewDevices
@Preview
@Composable
fun MenuOptionButtonPreview() {
    SolitairePreview {
        Box(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()) {
            MenuOptionButton(
                option = MenuState.Stats,
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .background(Color.Gray)
            )
        }
    }
}