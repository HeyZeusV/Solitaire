package com.heyzeusv.solitaire.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.MenuHeaderBar
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.formatTimeStats
import com.heyzeusv.solitaire.util.getAverageMoves
import com.heyzeusv.solitaire.util.getAverageScore
import com.heyzeusv.solitaire.util.getAverageTime
import com.heyzeusv.solitaire.util.getScorePercentage
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.getWinPercentage

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of selected game.
 */
@Composable
fun StatsMenu(
    menuVM: MenuViewModel
) {
    val selectedGame by menuVM.selectedGame.collectAsState()
    val stats by menuVM.stats.collectAsState()
    val currentGameStats =
        stats.statsList.find { it.game == selectedGame.dataStoreEnum } ?: getStatsDefaultInstance()

    StatsMenu(
        updateMenuState = menuVM::updateMenuState,
        stats = currentGameStats
    )
}

/**
 *  Composable that displays Stats Menu Screen where users can see [GameStats] of selected game.
 *  All the data has been hoisted into above [StatsMenu] thus allowing for easier testing.
 *  [StatsMenu] can be opened and closed by updating [MenuState] value using [updateMenuState].
 *  [stats] are to be displayed.
 */
@Composable
fun StatsMenu(
    updateMenuState: (MenuState) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MenuHeaderBar(
                menu = MenuState.STATS,
                onBackPress = { updateMenuState(MenuState.BUTTONS) }
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

@Preview
@Composable
fun StatsMenuPreview() {
    SolitairePreview {
        StatsMenu(
            updateMenuState = { },
            stats = GameStats.getDefaultInstance()
        )
    }
}