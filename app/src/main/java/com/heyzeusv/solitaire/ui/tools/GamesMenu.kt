package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
@Composable
fun GamesMenu(
    updateMenuState: (MenuState) -> Unit,
    updateStats: (LastGameStats) -> Unit,
    lgs: LastGameStats,
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    reset: () -> Unit,
) {
    var displayGameSwitch by remember { mutableStateOf(false) }
    var menuSelectedGame by remember { mutableStateOf(selectedGame) }
    var newlySelectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_THREE) }
    val gamesInfoOnClick = { game: Games ->
        if (game != menuSelectedGame) {
            if (lgs.moves > 0) {
                displayGameSwitch = true
                newlySelectedGame = game
            } else {
                menuSelectedGame = game
                reset()
            }
        }
    }

    BackHandler {
        updateMenuState(MenuState.BUTTONS)
//        updateSelectedGame(menuSelectedGame)
    }

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
            .testTag("Games Menu"),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val gamesHeader = stringResource(R.string.menu_header_games)
                Icon(
                    painter = painterResource(R.drawable.button_menu_back),
                    contentDescription = stringResource(R.string.menu_cdesc_close, gamesHeader),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 3.dp)
                        .size(50.dp)
                        .clickable {
                            updateMenuState(MenuState.BUTTONS)
//                            updateSelectedGame(menuSelectedGame)
                        }
                )
                Text(
                    text = gamesHeader,
                    modifier = Modifier.align(Alignment.Center),
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.displayMedium
                )
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(Games.entries) { game ->
                    GamesInfo(
                        game = game,
                        selected = menuSelectedGame == game,
                        onClick = gamesInfoOnClick
                    )
                }
            }
        }
    }
}

@Composable
fun GamesInfo(
    game: Games,
    selected: Boolean,
    onClick: (Games) -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick.invoke(game) },
        shape = ShapeDefaults.Small,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Purple40 else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val family = stringResource(game.familyId)
            val redeals = stringResource(game.redeals.nameId)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(game.nameId),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = stringResource(R.string.games_family, family))
                Text(text = stringResource(R.string.redeal, redeals))
            }
            Image(
                painter = painterResource(game.iconId),
                contentDescription = stringResource(game.nameId),
                modifier = Modifier
                    .padding(8.dp)
                    .height(150.dp)
                    .width(150.dp),
                contentScale = ContentScale.FillBounds
            )
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

@Preview
@Composable
fun GamesInfoPreview() {
    SolitairePreview {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            GamesInfo(
                game = Games.KLONDIKE_TURN_ONE,
                true
            ) { }
            Divider()
            GamesInfo(
                game = Games.YUKON,
                false
            ) { }
        }
    }
}