package com.heyzeusv.solitaire.ui.toolbar.menu

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.board.games.AustralianPatience
import com.heyzeusv.solitaire.ui.board.games.Games
import com.heyzeusv.solitaire.ui.board.games.KlondikeTurnOne
import com.heyzeusv.solitaire.ui.toolbar.MenuViewModel
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
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
    val settings by menuVM.settings.collectAsState()
    var selectedGame by remember { mutableStateOf(Games.getGameClass(settings.selectedGame)) }
    val stats by menuVM.stats.collectAsState()
    val selectedGameStats =
        stats.statsList.find { it.game == selectedGame.dataStoreEnum }
            ?: getStatsDefaultInstance()

    StatsMenu(
        selectedGame = selectedGame,
        updateSelectedGameStats = { selectedGame = it },
        selectedGameStats = selectedGameStats,
        onBackPressed = { menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen) }
    )
}

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of [selectedGame]
 *  which is updated through [updateSelectedGameStats]. All the data has been hoisted into above
 *  [StatsMenu] thus allowing for easier testing. [onBackPressed] handles opening and closing
 *  [StatsMenu]. [selectedGameStats] are to be displayed.
 */
@Composable
fun StatsMenu(
    selectedGame: Games,
    updateSelectedGameStats: (Games) -> Unit,
    selectedGameStats: GameStats,
    onBackPressed: () -> Unit
) {
    MenuScreen(
        menu = MenuState.Stats,
        modifier = Modifier.testTag("Stats Menu"),
        onBackPress = onBackPressed
    ) {
        StatsDropDownMenu(
            selectedGame = selectedGame,
            updateSelectedGame = { updateSelectedGameStats(it) }
        )
        StatColumn(
            selectedGameStats = selectedGameStats,
            game = selectedGame
        )
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
                    }
                    .testTag("DropDownMenu"),
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
                Games.orderedSubclasses.forEach { game ->
                    DropdownMenuItem(
                        text = { Text(stringResource(game.nameId)) },
                        onClick = {
                            updateSelectedGame(game)
                            expanded = false
                        },
                        modifier = Modifier.testTag("DropDownMenu Item ${game::class.java.simpleName}")
                    )
                }
            }
        }
    }
}

/**
 *  Composable which displays all stats stored in given [selectedGameStats] plus a few extras
 *  thanks to extension functions.
 */
@Composable
fun StatColumn(
    selectedGameStats: GameStats,
    game: Games
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        StatField(
            statNameId = R.string.stats_games_played,
            statValue = "${selectedGameStats.gamesPlayed}"
        )
        StatField(
            statNameId = R.string.stats_games_won,
            statValue = stringResource(
                R.string.stats_games_won_value,
                selectedGameStats.gamesWon,
                selectedGameStats.getWinPercentage()
            )
        )
        StatField(
            statNameId = R.string.stats_lowest_moves,
            statValue = "${selectedGameStats.lowestMoves}"
        )
        StatField(
            statNameId = R.string.stats_average_moves,
            statValue = "${selectedGameStats.getAverageMoves()}"
        )
        StatField(
            statNameId = R.string.stats_total_moves,
            statValue = "${selectedGameStats.totalMoves}"
        )
        StatField(
            statNameId = R.string.stats_fastest_win,
            statValue = selectedGameStats.fastestWin.formatTimeStats()
        )
        StatField(
            statNameId = R.string.stats_average_time,
            statValue = selectedGameStats.getAverageTime().formatTimeStats()
        )
        StatField(
            statNameId = R.string.stats_total_time,
            statValue = selectedGameStats.totalTime.formatTimeStats()
        )
        StatField(
            statNameId = R.string.stats_average_score,
            statValue = stringResource(
                R.string.stats_average_score_value,
                selectedGameStats.getAverageScore(),
                game.maxScore.amount,
                selectedGameStats.getScorePercentage(game.maxScore)
            )
        )
        StatField(
            statNameId = R.string.stats_best_score,
            statValue = "${selectedGameStats.bestTotalScore}",
            statTipId = R.string.stats_best_score_tip
        )
    }
}

/**
 *  Composable that displays a single stat. It contains its name [statNameId], a small tip
 *  [statTipId] if given, and the [statValue].
 */
@Composable
fun StatField(
    @StringRes statNameId: Int,
    statValue: String,
    @StringRes statTipId: Int? = null,
) {
    val statName = stringResource(statNameId)
    Column(modifier = Modifier.testTag("$statName: $statValue")){
        Text(
            text = statName,
            style = MaterialTheme.typography.bodyMedium
        )
        if (statTipId != null) {
            Text(
                text = stringResource(statTipId),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = statValue,
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            color = Purple80
        )
    }
}

@Preview
@Composable
fun StatsMenuPreview() {
    PreviewUtil().apply {
        Preview {
            StatsMenu(
                selectedGame = KlondikeTurnOne,
                updateSelectedGameStats = { },
                selectedGameStats = GameStats.getDefaultInstance()
            ) { }
        }
    }
}

@Preview
@Composable
fun StatsDropDownMenuPreview() {
    PreviewUtil().apply {
        Preview {
            StatsDropDownMenu(
                selectedGame = AustralianPatience,
                updateSelectedGame = { }
            )
        }
    }
}

@Preview
@Composable
fun StatFieldPreview() {
    PreviewUtil().apply {
        Preview {
            val test1 = 100
            val test2 = 100000f
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Column {
                    StatField(
                        statNameId = R.string.stats_average_score,
                        statValue = "$test1"
                    )
                    StatField(
                        statNameId = R.string.stats_best_score,
                        statValue = "$test2",
                        statTipId = R.string.stats_best_score_tip
                    )
                }
            }
        }
    }
}