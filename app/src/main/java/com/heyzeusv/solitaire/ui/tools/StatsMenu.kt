package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.MenuHeaderBar
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.formatTimeStats
import com.heyzeusv.solitaire.util.getAverageMoves
import com.heyzeusv.solitaire.util.getAverageScore
import com.heyzeusv.solitaire.util.getAverageTime
import com.heyzeusv.solitaire.util.getScorePercentage
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.getWinPercentage
import com.heyzeusv.solitaire.util.theme.Purple80

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of selected game.
 */
@Composable
fun StatsMenu(
    menuVM: MenuViewModel
) {
    val selectedGame by menuVM.statsSelectedGame.collectAsState()
    val stats by menuVM.stats.collectAsState()
    val selectedGameStats =
        stats.statsList.find { it.game == selectedGame.dataStoreEnum } ?: getStatsDefaultInstance()

    StatsMenu(
        updateMenuState = menuVM::updateMenuState,
        selectedGame = selectedGame,
        updateSelectedGame = menuVM::updateStatsSelectedGame,
        stats = selectedGameStats
    )
}

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of [selectedGame]
 *  which is updated through [updateSelectedGame]. All the data has been hoisted into above
 *  [StatsMenu] thus allowing for easier testing. [StatsMenu] can be opened and closed by updating
 *  [MenuState] value using [updateMenuState]. [stats] are to be displayed.
 */
@Composable
fun StatsMenu(
    updateMenuState: (MenuState) -> Unit,
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    stats: GameStats
) {
    val scrollableState = rememberScrollState()

    BackHandler { updateMenuState(MenuState.BUTTONS) }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Stats Menu"),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp)
                .scrollable(state = scrollableState, orientation = Orientation.Vertical),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MenuHeaderBar(
                menu = MenuState.STATS,
                onBackPress = { updateMenuState(MenuState.BUTTONS) }
            )
            StatsDropDownMenu(
                selectedGame = selectedGame,
                updateSelectedGame = { updateSelectedGame(it) }
            )
            Text(
                text = stringResource(
                    R.string.menu_content_stats,
                    stats.gamesPlayed,
                    stats.gamesWon,
                    stats.getWinPercentage(),
                    stats.lowestMoves,
                    stats.getAverageMoves(),
                    stats.totalMoves,
                    stats.fastestWin.formatTimeStats(),
                    stats.getAverageTime().formatTimeStats(),
                    stats.totalTime.formatTimeStats(),
                    stats.getAverageScore(),
                    stats.getScorePercentage(),
                    stats.bestTotalScore
                ),
                lineHeight = TextUnit(32f, TextUnitType.Sp),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.menu_tip_totalscore),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 *  [DropdownMenu] Composable that displays [Games] as its [DropdownMenuItem]s. [selectedGame] is
 *  the currently selected game. Pressing on any [DropdownMenuItem] calls [updateSelectedGame].
 */
@Composable
fun StatsDropDownMenu(
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit
) {
    // used to make sure DropdownMenuItems are the same size as OutlinedTextField
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val iconRotation = remember { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    if (interactionSource.collectIsPressedAsState().value) expanded = !expanded

    LaunchedEffect(expanded) {
        if (expanded) {
            iconRotation.animateTo(
                targetValue = 180f
            )
        } else {
            iconRotation.animateTo(0f)
        }
    }
    DisableSelection {
        Column {
            OutlinedTextField(
                value = stringResource(selectedGame.nameId),
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                readOnly = true,
                textStyle = MaterialTheme.typography.titleMedium,
                label = { Text(stringResource(R.string.stats_label_ddm)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.stats_cdesc_ddm),
                        modifier = Modifier.rotate(iconRotation.value),
                        tint = Purple80
                    )
                },
                interactionSource = interactionSource,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Purple80,
                    unfocusedTextColor = Purple80,
                    focusedBorderColor = Purple80,
                    unfocusedBorderColor = Purple80,
                    focusedLabelColor = Purple80,
                    unfocusedLabelColor = Purple80,
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                Games.entries.forEach { game ->
                    DropdownMenuItem(
                        text = { Text(stringResource(game.nameId)) },
                        onClick = {
                            updateSelectedGame(game)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StatsMenuPreview() {
    SolitairePreview {
        StatsMenu(
            updateMenuState = { },
            selectedGame = Games.KLONDIKE_TURN_ONE,
            updateSelectedGame = { },
            stats = GameStats.getDefaultInstance()
        )
    }
}

@Preview
@Composable
fun StatsDropDownMenuPreview() {
    SolitairePreview {
        StatsDropDownMenu(
            selectedGame = Games.AUSTRALIAN_PATIENCE,
            updateSelectedGame = { }
        )
    }
}