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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.ui.SolitaireButton
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview

@Composable
fun MenuContainer(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val displayMenu by menuVM.displayMenuButtons.collectAsState()
    val menuState by menuVM.menuState.collectAsState()

    val transition = updateTransition(targetState = menuState, label = "Menu Transition")
    val backgroundColor by transition.animateColor(label = "Menu backgroundColor Transform") { state ->
        when (state) {
            MenuState.BUTTONS -> Color.Transparent
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    }
    val cornerRadius by transition.animateDp(
        label = "Menu cornerRadius Transform",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 400,
                    easing = EaseOutCubic
                )
                else -> tween(
                    durationMillis = 200,
                    easing = EaseInCubic
                )
            }
        }
    ) { state ->
        when (state) {
            MenuState.BUTTONS -> 20.dp
            else -> 0.dp
        }
    }
    val elevation by transition.animateDp(
        label = "elevation",
        transitionSpec = {
            when (targetState) {
                MenuState.BUTTONS -> tween(
                    durationMillis = 200,
                    easing = EaseOutCubic,
                )

                else -> tween(
                    durationMillis = 200,
                    easing = EaseOutCubic,
                )
            }
        }
    ) { state ->
        when (state) {
            MenuState.BUTTONS -> 1.dp
            else -> 0.dp
        }
    }
    val paddingStart by transition.animateDp(label = "Menu paddingStart Transform") { state ->
        when (state) {
            MenuState.BUTTONS -> 12.dp
            else -> 0.dp
        }
    }
    val paddingBottom by transition.animateDp(label = "Menu paddingBottom Transform") { state ->
        when (state) {
            MenuState.BUTTONS -> 72.dp + 8.dp
            else -> 0.dp
        }
    }

    transition.AnimatedContent(
        modifier = modifier
            .padding(start = paddingStart, bottom = paddingBottom)
            .shadow(
                elevation = elevation, shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Transparent,
                spotColor = Color.Transparent
            )
            .drawBehind { drawRect(backgroundColor) },
        transitionSpec = {
            (fadeIn(animationSpec = tween(durationMillis = 250, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(durationMillis = 250)))
                .using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ ->
                    tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                }))
        }
    ) { state ->
        when (state) {
            MenuState.BUTTONS -> Fab {
                menuVM.updateMenuState(MenuState.STATS)
            }
            MenuState.STATS -> StatsScreen(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM)
            else -> {

            }
        }
    }
}

// TODO: MenuOptionButton should be built like this
@Composable
private fun Fab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = 76.dp,
                minHeight = 76.dp,
            )
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Filled.Add),
            contentDescription = null,
        )
    }
}

/**
 *  Composable that displays the various [MenuState] available to user. [displayMenu] determines
 *  if [MenuOptionButton]s should be displayed.
 */
@Composable
fun SolitaireMenuButtons(
    displayMenu: Boolean,
    updateMenuState: (MenuState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
//            .padding(start = 12.dp, bottom = 8.dp)
            .width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val menuButtons = listOf(MenuState.GAMES)
        menuButtons.forEach { option ->
            MenuOptionButton(
                displayMenu = displayMenu,
                option = option,
                onClick = updateMenuState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 *  [SolitaireButton] Composable wrapped in a [AnimatedVisibility] Composable. [displayMenu] causes
 *  enter/exit animation to start. [option] contains the text/icon data to be displayed. [onClick]
 *  is ran when pressed.
 */
@Composable
fun MenuOptionButton(
    displayMenu: Boolean,
    option: MenuState,
    onClick: (MenuState) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = displayMenu,
        modifier = modifier,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        SolitaireButton(
            onClick = { onClick(option) },
            icon = painterResource(option.iconId),
            iconContentDes = stringResource(option.iconDescId),
            buttonText = stringResource(option.nameId),
            modifier = Modifier.height(40.dp)
        )
    }
}


@Preview
@Composable
fun SolitaireMenuButtonsPreview() {
    SolitairePreview {
        SolitaireMenuButtons(
            displayMenu = true,
            updateMenuState = { }
        )
    }
}

@Preview
@Composable
fun MenuOptionButtonPreview() {
    SolitairePreview {
        MenuOptionButton(
            displayMenu = true,
            option = MenuState.GAMES,
            onClick = { }
        )
    }
}