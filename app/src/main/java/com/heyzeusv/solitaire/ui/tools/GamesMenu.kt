package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.GameSwitchAlertDialog
import com.heyzeusv.solitaire.ui.game.GameViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.theme.Purple40

/**
 *   Composable that displays Menu which allows user to switch games.
 */
@Composable
fun GamesMenu(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    menuVM: MenuViewModel
) {
    val selectedGame by menuVM.selectedGame.collectAsState()
    val lgs = sbVM.retrieveLastGameStats(false)

    GamesMenu(
        updateMenuState = menuVM::updateMenuState,
        updateStats = menuVM::updateStats,
        lgs = lgs,
        selectedGame = selectedGame,
        updateSelectedGame = menuVM::updateSelectedGame,
        reset = {
            gameVM.resetAll(ResetOptions.NEW)
            sbVM.reset()
        }
    )
}

/**
 *   Composable that displays Menu which allows user to switch games. All the data has been hoisted
 *   into above [GamesMenu] thus allowing for easier testing. [GamesMenu] can be opened and closed
 *   by updated [MenuState] value using [updateMenuState]. [lgs] is used to check if a game has been
 *   started and the user is trying to switch game types causing a [GameSwitchAlertDialog] to appear
 *   to confirm game change, as well as to [updateStats] if user confirms game switch with more than
 *   1 move taken. [reset] is called when game change is confirmed causing game board to reset.
 *   [selectedGame] determines which stats to be displayed and which game to be shown when user
 *   close Menu and is updated by [updateSelectedGame].
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GamesMenu(
    updateMenuState: (MenuState) -> Unit,
    updateStats: (LastGameStats) -> Unit,
    lgs: LastGameStats,
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    reset: () -> Unit,
) {
    BackHandler { updateMenuState(MenuState.BUTTONS) }

    var displayGameSwitch by remember { mutableStateOf(false) }
    var newlySelectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_THREE) }

    GameSwitchAlertDialog(
        displayGameSwitch = displayGameSwitch,
        confirmOnClick = {
            if (lgs.moves > 1) updateStats(lgs)
            updateSelectedGame(newlySelectedGame)
            reset()
            displayGameSwitch = false
        },
        dismissOnClick = { displayGameSwitch = false }
    )
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Games Menu")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
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
                                    displayGameSwitch = true
                                    newlySelectedGame = it
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
        }
    }
}

@Preview
@Composable
fun GamesMenuPreview() {
    SolitairePreview {
        GamesMenu(
            updateMenuState = { },
            updateStats = { },
            lgs = LastGameStats(false, 0, 0, 0),
            selectedGame = Games.KLONDIKE_TURN_ONE,
            updateSelectedGame = { },
            reset = { }
        )
    }
}