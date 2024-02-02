package com.heyzeusv.solitaire.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.data.Stats
import com.heyzeusv.solitaire.ui.theme.BackgroundOverlay
import com.heyzeusv.solitaire.ui.theme.Purple40
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.formatTimeStats

/**
 *  Composable that displays Menu which allows user to switch games and view stats for selected game.
 */
@Composable
fun SolitaireMenu(
    menuVM: MenuViewModel
) {
    val displayMenu by menuVM.displayMenu.collectAsState()
    val selectedGame by menuVM.selectedGame.collectAsState()
    val stats by menuVM.stats.collectAsState()

    SolitaireMenu(
        displayMenu = displayMenu,
        updateDisplayMenu = menuVM::updateDisplayMenu,
        selectedGame = selectedGame,
        updateSelectedGame = menuVM::updateSelectedGame,
        stats = stats
    )
}

/**
 *  Composable that displays Menu which allows user to switch games and view stats for selected game.
 *  All the data has been hoisted into above [SolitaireMenu] thus allowing for easier testing.
 *  [displayMenu] determines if Composable should be displayed to user and is updated by
 *  [updateDisplayMenu]. [selectedGame] determines which stats to be displayed and which game to be
 *  shown when user close Menu and is updated by [updateSelectedGame]. [stats] are to be displayed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SolitaireMenu(
    displayMenu: Boolean,
    updateDisplayMenu: (Boolean) -> Unit,
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    stats: Stats
) {
    val scrollableState = rememberScrollState()

    BackHandler(displayMenu) {
        updateDisplayMenu(false)
    }

    if (displayMenu) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable { updateDisplayMenu(false) },
            color = BackgroundOverlay
        ) {}
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(all = 32.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 24.dp)
                    .scrollable(state = scrollableState, orientation = Orientation.Vertical),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Games",
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Games.entries.forEach {
                        FilterChip(
                            selected = it == selectedGame,
                            onClick = { updateSelectedGame(it) },
                            label = { Text(text = it.gameName) },
                            leadingIcon = {
                                if (it == selectedGame) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "${it.gameName} is selected",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple40
                            )
                        )
                    }
                }
                Text(
                    text = "Stats",
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                val statsString = """Games Played: ${stats.gamesPlayed}
                        |Games Won: ${stats.gamesWon} (${stats.getGamePercent()}%)
                        |Lowest Moves in Win: ${stats.lowestMoves}
                        |Average Moves: ${stats.averageMoves}
                        |Total Moves: ${stats.totalMoves}
                        |Fastest Win: ${stats.fastestWin.formatTimeStats()}
                        |Average Time: ${stats.averageTime.formatTimeStats()}
                        |Total Time Played: ${stats.totalTime.formatTimeStats()}
                        |Average Score: ${stats.averageScore} of 52 (${stats.getScorePercent()}%)
                        |Best Total Score: ${stats.bestTotalScore}""".trimMargin()
                Text(text = statsString)
                Text(
                    text = "\u2517 Moves + Time + Score (Lower is better)",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Credits",
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(text = "Card Suits by Amethyst Studio from https://thenounproject.com/icon/card-suits-5874222/ (CC BY 3.0)" )
            }
        }
    }
}

@Preview
@Composable
fun SolitaireMenuPreview() {
    SolitairePreview {
        SolitaireMenu(
            displayMenu = true,
            updateDisplayMenu = { },
            selectedGame = Games.KLONDIKETURNONE,
            updateSelectedGame = { },
            stats = Stats()
        )
    }
}