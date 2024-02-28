package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.GameSwitchAlertDialog
import com.heyzeusv.solitaire.ui.MenuHeaderBar
import com.heyzeusv.solitaire.ui.game.SolitaireBoard
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.theme.Purple40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *   Composable that displays Menu which allows user to switch games.
 */
@Composable
fun GamesMenu(
    sbVM: ScoreboardViewModel,
    menuVM: MenuViewModel
) {
    val selectedGame by menuVM.selectedGame.collectAsState()
    val lgs = sbVM.retrieveLastGameStats(false)
    val scope = rememberCoroutineScope()

    GamesMenu(
        updateStats = menuVM::updateStats,
        lgs = lgs,
        selectedGame = selectedGame,
        onBackPress = { game ->
            scope.launch {
                menuVM.updateSelectedGame(game)
                delay(300)
                menuVM.updateMenuState(MenuState.BUTTONS)
            }
        }
    )
}

/**
 *   Composable that displays Menu which allows user to switch games. All the data has been hoisted
 *   into above [GamesMenu] thus allowing for easier testing. [lgs] is used to check if a game has
 *   been started and the user is trying to switch game types causing a [GameSwitchAlertDialog] to
 *   appear to confirm game change, as well as to [updateStats] if user confirms game switch with
 *   more than 1 move taken. [selectedGame] determines which game is selected when user opens
 *   [GamesMenu]. [onBackPress] is launched when user tries to close [GamesMenu] using either top
 *   left arrow icon or back button on phone; it updates [selectedGame] which causes
 *   [SolitaireBoard] to recompose, so small delay is added before closing [GamesMenu] by updating
 *   [MenuState].
 */
@Composable
fun GamesMenu(
    updateStats: (LastGameStats) -> Unit,
    lgs: LastGameStats,
    selectedGame: Games,
    onBackPress: (Games) -> Unit
) {
    var displayGameSwitch by remember { mutableStateOf(false) }
    var menuSelectedGame by remember { mutableStateOf(selectedGame) }
    var inProgressSelectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_THREE) }
    val gamesInfoOnClick = { game: Games ->
        if (game != menuSelectedGame) {
            if (lgs.moves > 0) {
                displayGameSwitch = true
                inProgressSelectedGame = game
            } else {
                menuSelectedGame = game
            }
        }
    }
    val lazyColumnState = rememberLazyListState()

    BackHandler {
        onBackPress(menuSelectedGame)
    }
    LaunchedEffect(key1 = Unit) {
        lazyColumnState.animateScrollToItem(selectedGame.ordinal)
    }
    GameSwitchAlertDialog(
        displayGameSwitch = displayGameSwitch,
        confirmOnClick = {
            if (lgs.moves > 1) updateStats(lgs)
            menuSelectedGame = inProgressSelectedGame
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
            MenuHeaderBar(
                menu = MenuState.GAMES,
                onBackPress = { onBackPress(menuSelectedGame) }
            )
            LazyColumn(
                state = lazyColumnState,
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

/**
 *  Displays given [game] info, name, family, redeals, and preview image. [selected] determines the
 *  background color which lets the user know which game will be loaded. [onClick] is called when
 *  user clicks [GamesInfo]
 */
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
            updateStats = { },
            lgs = LastGameStats(false, 0, 0, 0),
            selectedGame = Games.KLONDIKE_TURN_ONE,
            onBackPress = { }
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