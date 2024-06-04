package com.heyzeusv.solitaire.menu.stats

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.games.All
import com.heyzeusv.solitaire.games.AustralianPatience
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.menu.MenuScreen
import com.heyzeusv.solitaire.menu.MenuViewModel
import com.heyzeusv.solitaire.service.AccountStatus
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.composables.AccountStatusIndicator
import com.heyzeusv.solitaire.util.composables.HorizontalPagerIndicator
import com.heyzeusv.solitaire.util.composables.SolitaireAlertDialog
import com.heyzeusv.solitaire.util.formatTimeStats
import com.heyzeusv.solitaire.util.getAverageMoves
import com.heyzeusv.solitaire.util.getAverageScore
import com.heyzeusv.solitaire.util.getAverageTime
import com.heyzeusv.solitaire.util.getScorePercentage
import com.heyzeusv.solitaire.util.getWinPercentage
import com.heyzeusv.solitaire.util.theme.Purple80

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of selected game.
 */
@Composable
fun StatsMenu(isConnected: Boolean, menuVM: MenuViewModel) {
    val settings by menuVM.settingsFlow.collectAsState()
    var selectedGame by remember { mutableStateOf(Games.from(settings.selectedGame)) }
    val stats by menuVM.statsFlow.collectAsState()
    val selectedGamePersonalStats =
        stats.statsList.find { it.game == selectedGame.dataStoreEnum }
            ?: getStatsDefaultInstance(selectedGame.dataStoreEnum)
    val selectedGameGlobalStats =
        stats.globalStatsList.find { it.game == selectedGame.dataStoreEnum }
            ?: getStatsDefaultInstance(selectedGame.dataStoreEnum)
    val accountStatus by menuVM.accountStatus.collectAsState()

    StatsMenu(
        accountStatus = accountStatus,
        selectedGame = selectedGame,
        updateSelectedGame = { selectedGame = it },
        selectedGamePersonalStats = selectedGamePersonalStats,
        selectedGameGlobalStats = selectedGameGlobalStats,
        onBackPressed = { menuVM.updateDisplayMenuButtonsAndMenuState(MenuState.ButtonsFromScreen) },
        syncStatsOnClick = { menuVM.syncStatsOnClick(isConnected) }
    ) { menuVM.syncStatsConfirmOnClick() }
}

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of [selectedGame]
 *  which is updated through [updateSelectedGame]. All the data has been hoisted into above
 *  [StatsMenu] thus allowing for easier testing. [onBackPressed] handles opening and closing
 *  [StatsMenu]. [selectedGamePersonalStats] are to be displayed. [syncStatsOnClick] runs when clicking
 *  on upload icon. [syncStatsConfirmOnClick] runs when confirming [SolitaireAlertDialog] that
 *  appears on upload button press.
 */
@Composable
fun StatsMenu(
    accountStatus: AccountStatus = AccountStatus.Idle(),
    selectedGame: Games,
    updateSelectedGame: (Games) -> Unit,
    selectedGamePersonalStats: GameStats,
    selectedGameGlobalStats: GameStats,
    onBackPressed: () -> Unit = { },
    syncStatsOnClick: () -> Boolean = { true },
    syncStatsConfirmOnClick: () -> Unit = { }
) {
    var displaySyncStatsAD by remember { mutableStateOf(false) }
    AccountStatusIndicator(accountStatus)
    MenuScreen(
        menu = MenuState.Stats,
        modifier = Modifier.testTag("Stats Menu"),
        onBackPress = onBackPressed,
        extraAction = { modifier ->
            Icon(
                painter = painterResource(R.drawable.button_sync),
                contentDescription = stringResource(R.string.menu_cdesc_sync),
                modifier = modifier
                    .padding(top = dimensionResource(R.dimen.mhbIconPaddingTop))
                    .size(dimensionResource(R.dimen.mhbIconSize))
                    .clickable { displaySyncStatsAD = syncStatsOnClick() }
            )
        }
    ) {
        StatsDropDownMenu(
            selectedGame = selectedGame,
            updateSelectedGame = { updateSelectedGame(it) }
        )
        StatPager(
            selectedGamePersonalStats = selectedGamePersonalStats,
            selectedGameGlobalStats = selectedGameGlobalStats,
            game = selectedGame
        )
    }
    SolitaireAlertDialog(
        display = displaySyncStatsAD,
        title = stringResource(R.string.sync_ad_title),
        message = stringResource(R.string.sync_ad_message),
        confirmText = stringResource(R.string.sync_ad_confirm),
        confirmOnClick = {
            syncStatsConfirmOnClick()
            displaySyncStatsAD = false
        },
        dismissText = stringResource(R.string.sync_ad_dismiss),
        dismissOnClick = { displaySyncStatsAD = false }
    )
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
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

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
        Box {
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
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    .height(screenHeight - 225.dp),
            ) {
                HorizontalDivider()
                Games.statsOrderedSubclasses.forEach { game ->
                    DropdownMenuItem(
                        text = { Text(stringResource(game.nameId)) },
                        onClick = {
                            updateSelectedGame(game)
                            expanded = false
                        },
                        modifier = Modifier
                            .testTag("DropDownMenu Item ${game::class.java.simpleName}")
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatPager(
    selectedGamePersonalStats: GameStats,
    selectedGameGlobalStats: GameStats,
    game: Games
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        ) { page ->
            when (page) {
                0 -> StatColumn(
                    title = stringResource(R.string.stats_personal),
                    selectedGameStats = selectedGamePersonalStats,
                    game = game,
                    scrollState = scrollState
                )
                else -> StatColumn(
                    title = stringResource(R.string.stats_global),
                    selectedGameStats = selectedGameGlobalStats,
                    game = game,
                    scrollState = scrollState
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = 2,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .align(Alignment.CenterHorizontally),
            activeColor = Purple80,
            inactiveColor = Purple80.copy(alpha = 0.38f)
        )
    }
}

/**
 *  Composable which displays all stats stored in given [selectedGameStats] plus a few extras
 *  thanks to extension functions.
 */
@Composable
fun StatColumn(
    title: String,
    selectedGameStats: GameStats,
    game: Games,
    scrollState: ScrollState
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.headlineLarge
        )
        Column(
            modifier = Modifier.verticalScroll(scrollState),
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
            if (game != All) {
                StatField(
                    statNameId = R.string.stats_average_score,
                    statValue = stringResource(
                        R.string.stats_average_score_value,
                        selectedGameStats.getAverageScore(),
                        game.maxScore.amount,
                        selectedGameStats.getScorePercentage(game.maxScore)
                    )
                )
            }
            StatField(
                statNameId = R.string.stats_best_combined_score,
                statValue = "${selectedGameStats.bestCombinedScore}",
                statTipId = R.string.stats_best_combined_score_tip
            )
        }
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
                updateSelectedGame = { },
                selectedGamePersonalStats = GameStats.getDefaultInstance(),
                selectedGameGlobalStats = GameStats.getDefaultInstance()
            )
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
                        statNameId = R.string.stats_best_combined_score,
                        statValue = "$test2",
                        statTipId = R.string.stats_best_combined_score_tip
                    )
                }
            }
        }
    }
}