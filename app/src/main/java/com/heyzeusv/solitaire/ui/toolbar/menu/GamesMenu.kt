package com.heyzeusv.solitaire.ui.toolbar.menu

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
import com.heyzeusv.solitaire.ui.GameSwitchAlertDialog
import com.heyzeusv.solitaire.ui.board.BoardLayout
import com.heyzeusv.solitaire.ui.board.games.Games
import com.heyzeusv.solitaire.ui.board.games.KlondikeTurnOne
import com.heyzeusv.solitaire.ui.board.games.Yukon
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.ui.toolbar.MenuViewModel
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.theme.Purple40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 *  Composable that displays Menu which allows user to switch games.
 */
@Composable
fun GamesMenu(
    sbVM: ScoreboardViewModel,
    menuVM: MenuViewModel
) {
    val settings by menuVM.settings.collectAsState()
    val selectedGame = Games.getGameClass(settings.selectedGame)
    val scope = rememberCoroutineScope()

    GamesMenu(
        gameSwitchConfirmOnClick = {
            val lgs = sbVM.retrieveLastGameStats(false)
            if (lgs.moves > 1) {
                menuVM.updateStats(lgs)
                sbVM.reset()
            }
        },
        gameInfoOnClickCheck = { sbVM.retrieveLastGameStats(false).moves > 0 },
        selectedGame = selectedGame
    ) { game ->
        scope.launch {
            if (game != selectedGame) {
                menuVM.updateSelectedGame(game)
                sbVM.reset()
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
 *   [BoardLayout] to recompose, so small delay is added before closing [GamesMenu] by updating
 *   [MenuState].
 */
@Composable
fun GamesMenu(
    gameSwitchConfirmOnClick: () -> Unit,
    gameInfoOnClickCheck: () -> Boolean,
    selectedGame: KClass<out Games>,
    onBackPress: (KClass<out Games>) -> Unit
) {
    var displayGameSwitch by remember { mutableStateOf(false) }
    var menuSelectedGame by remember { mutableStateOf(selectedGame) }
    var inProgressSelectedGame by remember { mutableStateOf<KClass<out Games>>(Yukon::class) }
    val gamesInfoOnClick = { game: KClass<out Games> ->
        if (game != menuSelectedGame) {
            if (gameInfoOnClickCheck()) {
                displayGameSwitch = true
                inProgressSelectedGame = game
            } else {
                menuSelectedGame = game
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
        },
        dismissOnClick = { displayGameSwitch = false }
    )
    MenuScreen(
        menu = MenuState.Games,
        modifier = Modifier.testTag("Games Menu"),
        onBackPress = { onBackPress(menuSelectedGame) }
    ) {
        LazyColumn(
            modifier = Modifier.testTag("Games Menu List"),
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.gColumnSpacedBy))
        ) {
            items(Games.orderedSubclasses) { game ->
                val instance = game.createInstance()
                GamesInfo(
                    game = instance,
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
                selectedGame = KlondikeTurnOne::class
            ) { }
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
                    game = KlondikeTurnOne(),
                    true
                ) { }
                HorizontalDivider()
                GamesInfo(
                    game = Yukon(),
                    false
                ) { }
            }
        }
    }
}