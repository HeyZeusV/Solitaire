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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.theme.BackgroundOverlay
import com.heyzeusv.solitaire.ui.theme.Pink80
import com.heyzeusv.solitaire.ui.theme.Purple40
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.LinkifyText
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.formatTimeStats
import com.heyzeusv.solitaire.util.getAverageMoves
import com.heyzeusv.solitaire.util.getAverageScore
import com.heyzeusv.solitaire.util.getAverageTime
import com.heyzeusv.solitaire.util.getScorePercentage
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.getWinPercentage

/**
 *  Composable that displays Menu which allows user to switch games and view stats for selected game.
 */
@Composable
fun SolitaireMenu(
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
) {
    val displayMenu by menuVM.displayMenu.collectAsState()
    val selectedGame by menuVM.selectedGame.collectAsState()
    val stats by menuVM.stats.collectAsState()
    val currentGameStats =
        stats.statsList.find { it.game == selectedGame.dataStoreEnum } ?: getStatsDefaultInstance()

    SolitaireMenu(
        displayMenu = displayMenu,
        updateDisplayMenu = menuVM::updateDisplayMenu,
        lgs = gameVM.retrieveLastGameStats(false),
        selectedGame = selectedGame,
        updateSelectedGame = menuVM::updateSelectedGame,
        updateStats = menuVM::updateStats,
        reset = { gameVM.reset(ResetOptions.NEW) },
        stats = currentGameStats
    )
}

/**
 *  Composable that displays Menu which allows user to switch games and view stats for selected game.
 *  All the data has been hoisted into above [SolitaireMenu] thus allowing for easier testing.
 *  Menu can be opened and closed by updating [displayMenu] value using [updateDisplayMenu]. [lgs] is
 *  used to check if a game has been started and the user is trying to switch game types causing an
 *  AlertDialog to appear to confirm game change, as well as to [updateStats] if user confirms game
 *  switch with more than 1 move taken. [reset] is called when game change is confirmed causing
 *  game board to reset. [selectedGame] determines which stats to be displayed and which game to
 *  be shown when user close Menu and is updated by [updateSelectedGame]. [stats] are to be displayed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SolitaireMenu(
    displayMenu: Boolean,
    updateDisplayMenu: (Boolean) -> Unit,
    lgs: LastGameStats,
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    updateStats: (LastGameStats) -> Unit,
    reset: () -> Unit,
    stats: GameStats
) {

    if (displayMenu) {
        val scrollableState = rememberScrollState()

        var showGameSwitch by remember { mutableStateOf(false) }
        var newlySelectedGame by remember { mutableStateOf(Games.KLONDIKETURNONE) }

        BackHandler { updateDisplayMenu(false) }

        if (showGameSwitch) {
            AlertDialog(
                onDismissRequest = { showGameSwitch = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (lgs.moves > 1) updateStats(lgs)
                        updateSelectedGame(newlySelectedGame)
                        showGameSwitch = false
                        reset()
                    }) {
                        Text(text = stringResource(R.string.games_ad_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGameSwitch = false }) {
                        Text(text = stringResource(R.string.games_ad_dismiss))
                    }
                },
                title = { Text(text = stringResource(R.string.games_ad_title)) },
                text = { Text(text = stringResource(R.string.games_ad_msg)) }
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable { updateDisplayMenu(false) }
                .testTag("Close Menu"),
            color = BackgroundOverlay
        ) {}
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(all = 32.dp)
                .testTag("Menu")
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 24.dp)
                    .scrollable(state = scrollableState, orientation = Orientation.Vertical),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu_header_games),
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
                            onClick = {
                                if (it != selectedGame) {
                                    if (lgs.moves > 0) {
                                        newlySelectedGame = it
                                        showGameSwitch = true
                                    } else {
                                        updateSelectedGame(it)
                                        reset()
                                    }
                                }
                            },
                            label = { Text(text = stringResource(it.gameName)) },
                            leadingIcon = {
                                if (it == selectedGame) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = stringResource(
                                            R.string.menu_cdesc_chip,
                                            stringResource(it.gameName)
                                        ),
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
                    text = stringResource(R.string.menu_header_stats),
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
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
                    )
                )
                Text(
                    text = stringResource(R.string.menu_tip_totalscore),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.menu_header_credits),
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                LinkifyText(
                    text = stringResource(R.string.menu_content_credits),
                    linkColor = Pink80
                )
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
            lgs = LastGameStats(false, 0, 0, 0),
            selectedGame = Games.KLONDIKETURNONE,
            updateSelectedGame = { },
            updateStats = { },
            reset = { },
            stats = GameStats.getDefaultInstance()
        )
    }
}