package com.heyzeusv.solitaire.menu

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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.GameViewModel
import com.heyzeusv.solitaire.menu.settings.SettingsMenu
import com.heyzeusv.solitaire.menu.stats.StatsMenu
import com.heyzeusv.solitaire.util.composables.BaseButton
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewDevices
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  Composable that displays Menu options which transition into Menu screens.
 */
@Composable
fun MenuContainer(
    isConnected: Boolean,
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
            option = MenuState.Games
        ) { GamesMenu(gameVM = gameVM, menuVM = menuVM) }
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.Rules
        ) {
            RulesMenu(selectedGame = gameVM.selectedGame) {
                menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
            }
        }
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.Stats
        ) { StatsMenu(isConnected, menuVM) }
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.Settings
        ) { SettingsMenu(isConnected, menuVM) }
        MenuOptionTransition(
            displayMenuButtons = displayMenuButtons,
            menuState = menuState,
            updateMenuState = menuVM::updateMenuState,
            option = MenuState.About,
            bottomPadding = dimensionResource(R.dimen.mbPaddingBottom),
            transformOrigin = TransformOrigin(0.5f, 0.20f)
        ) {
            AboutMenu {
                menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
            }
        }
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
    bottomPadding: Dp = dimensionResource(R.dimen.mbPaddingBottomDefault),
    transformOrigin: TransformOrigin = TransformOrigin.Center,
    content: @Composable () -> Unit
) { 
    val zeroDp = dimensionResource(R.dimen.zero)
    val menuButtonDuration = integerResource(R.integer.mButtonDuration)
    val menuButtonDelay = integerResource(R.integer.mButtonDelay)
    val menuButtonAniSpec = if (menuState == MenuState.ButtonsFromScreen) {
        tween<Float>(menuButtonDuration, menuButtonDelay)
    } else {
        tween(menuButtonDuration)
    }
    val menuOptionDuration = integerResource(R.integer.mOptionDuration)

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
            option -> zeroDp
            else -> dimensionResource(R.dimen.mbRoundRadius)
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
            option -> zeroDp
            else -> dimensionResource(R.dimen.buttonBorderWidth)
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
            option -> zeroDp
            else -> dimensionResource(R.dimen.buttonElevation)
        }
    }
    val paddingStart by transition.animateDp(
        label = "Menu ${option.name} paddingStart Transition"
    ) { state ->
        when (state) {
            option -> zeroDp
            else -> dimensionResource(R.dimen.mbPaddingStart)
        }
    }
    val paddingBottom by transition.animateDp(
        label = "Menu ${option.name} paddingBottom Transition"
    ) { state ->
        when (state) {
            MenuState.Buttons, MenuState.ButtonsFromScreen -> bottomPadding
            else -> zeroDp
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
                MenuState.Buttons, MenuState.ButtonsFromScreen -> BaseButton(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.mbHeight))
                        .fillMaxWidth(0.3f),
                    iconContentDesc = stringResource(option.iconDescId),
                    buttonText = stringResource(option.nameId),
                    iconImgVector = option.icon,
                ) { updateMenuState(option) }
                else -> {}
            }
        }
    }
}

@PreviewDevices
@Composable
fun MenuOptionTransitionPreview() {
    PreviewUtil().apply {
        Preview {
            MenuOptionTransition(
                displayMenuButtons = true,
                menuState = MenuState.Buttons,
                updateMenuState = { },
                option = MenuState.Settings
            ) { }
        }
    }
}