package com.heyzeusv.solitaire.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.Board
import com.heyzeusv.solitaire.board.GameViewModel
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.games.Yukon
import com.heyzeusv.solitaire.util.composables.GameSwitchAlertDialog
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.theme.Purple40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  Composable that displays Menu which allows user to switch games.
 */
@Composable
fun GamesMenu(
    gameVM: GameViewModel,
    menuVM: MenuViewModel
) {
    val settings by menuVM.settingsFlow.collectAsState()
    val selectedGame = Games.getGameClass(settings.selectedGame)
    val scope = rememberCoroutineScope()

    GamesMenu(
        gameSwitchConfirmOnClick = {
            val lgs = gameVM.sbLogic.retrieveLastGameStats(false)
            if (lgs.moves > 1) {
                menuVM.updateStats(lgs)
            }
        },
        gameInfoOnClickCheck = { gameVM.sbLogic.moves.value > 0 },
        selectedGame = selectedGame
    ) { game, gameChanged ->
        scope.launch {
            if (gameChanged) {
                menuVM.updateSelectedGame(game)
                delay(300)
            }
            menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen)
        }
    }
}

/**
 *   Composable that displays Menu which allows user to switch games. All the data has been hoisted
 *   into above [GamesMenu] thus allowing for easier testing. [gameInfoOnClickCheck] is used to
 *   check if a game has been started and the user is trying to switch game types causing a
 *   [GameSwitchAlertDialog] to appear to confirm/decline game change, running
 *   [gameSwitchConfirmOnClick] on confirm. [selectedGame] determines which game is selected when
 *   user opens [GamesMenu]. [onBackPress] is launched when user tries to close [GamesMenu] using
 *   either top left arrow icon or back button on phone; it updates [selectedGame] which causes
 *   [Board] to recompose, so small delay is added before closing [GamesMenu] by updating
 *   [MenuState].
 */
@Composable
fun GamesMenu(
    gameSwitchConfirmOnClick: () -> Unit,
    gameInfoOnClickCheck: () -> Boolean,
    selectedGame: Games,
    onBackPress: (Games, Boolean) -> Unit
) {
    var displayGameSwitch by remember { mutableStateOf(false) }
    var menuSelectedGame by remember { mutableStateOf(selectedGame) }
    var inProgressSelectedGame by remember { mutableStateOf<Games>(Yukon) }
    var gameChanged by remember { mutableStateOf(false) }
    val gamesInfoOnClick = { game: Games ->
        if (game != menuSelectedGame) {
            if (gameInfoOnClickCheck()) {
                displayGameSwitch = true
                inProgressSelectedGame = game
            } else {
                menuSelectedGame = game
                gameChanged = true
            }
        }
    }
    val lazyColumnState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        lazyColumnState.animateScrollToItem(Games.orderedSubclasses.indexOf(selectedGame))
    }
    GameSwitchAlertDialog(
        displayGameSwitch = displayGameSwitch,
        confirmOnClick = {
            gameSwitchConfirmOnClick()
            menuSelectedGame = inProgressSelectedGame
            displayGameSwitch = false
            gameChanged = true
        },
        dismissOnClick = { displayGameSwitch = false }
    )
    MenuScreen(
        menu = MenuState.Games,
        modifier = Modifier.testTag("Games Menu"),
        onBackPress = { onBackPress(menuSelectedGame, gameChanged) }
    ) {
        LazyColumn(
            modifier = Modifier.testTag("Games Menu List"),
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gColumnSpacedBy))
        ) {
            items(Games.orderedSubclasses) { game ->
                GamesInfo(
                    game = game,
                    selected = menuSelectedGame == game
                ) { gamesInfoOnClick(game) }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .testTag("${game::class.simpleName} Card $selected"),
        shape = ShapeDefaults.Small,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Purple40 else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.gInfoHeight)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val family = stringResource(game.familyId)
            val redeals = stringResource(game.redeals.nameId)
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.gInfoColumnPaddingAll))
                    .weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(dimensionResource(R.dimen.gInfoColumnSpacedBy))
            ) {
                Text(
                    text = stringResource(game.nameId),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = stringResource(R.string.games_family, family))
                Text(text = stringResource(R.string.redeal, redeals))
            }
            Image(
                painter = painterResource(game.previewId),
                contentDescription = stringResource(game.nameId),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.gInfoImagePaddingAll))
                    .size(dimensionResource(R.dimen.gInfoImageSize)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun GamesMenuPreview() {
    PreviewUtil().apply {
        Preview {
            GamesMenu(
                gameSwitchConfirmOnClick = { },
                gameInfoOnClickCheck = { true },
                selectedGame = KlondikeTurnOne
            ) { _, _ -> }
        }
    }
}

@Preview
@Composable
fun GamesInfoPreview() {
    PreviewUtil().apply {
        Preview {
            Column(modifier = Modifier.padding(all = 8.dp)) {
                GamesInfo(
                    game = KlondikeTurnOne,
                    true
                ) { }
                HorizontalDivider()
                GamesInfo(
                    game = Yukon,
                    false
                ) { }
            }
        }
    }
}