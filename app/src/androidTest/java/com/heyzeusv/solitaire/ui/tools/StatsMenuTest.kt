package com.heyzeusv.solitaire.ui.tools

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.games.Alaska
import com.heyzeusv.solitaire.games.AustralianPatience
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.KlondikeTurnOne
import com.heyzeusv.solitaire.menu.stats.StatsMenu
import com.heyzeusv.solitaire.menu.stats.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsMenuTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private var gameOneStats = getStatsDefaultInstance(Game.GAME_KLONDIKETURNONE).toBuilder()
        .setGamesPlayed(10)
        .setGamesWon(5)
        .setLowestMoves(30)
        .setTotalMoves(100)
        .setFastestWin(700L)
        .setTotalTime(10000L)
        .setTotalScore(200)
        .setBestCombinedScore(329L)
        .build()

    private var gameTwoStats = getStatsDefaultInstance(Game.GAME_KLONDIKETURNTHREE)

    @Test
    fun statsMenu_display() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    StatsMenu(
                        selectedGame = KlondikeTurnOne,
                        updateSelectedGame = { },
                        selectedGamePersonalStats = gameOneStats,
                        selectedGameGlobalStats = gameTwoStats
                    ) { }
                }
            }

            onNodeWithTextId(R.string.menu_button_stats).assertIsDisplayed()
            gameOneStatsDisplayed()
        }
    }

    @Test
    fun statsMenu_dropDownMenu_allGamesShown() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    StatsMenu(
                        selectedGame = KlondikeTurnOne,
                        updateSelectedGame = { },
                        selectedGamePersonalStats = gameOneStats,
                        selectedGameGlobalStats = gameTwoStats
                    ) { }
                }
            }

            onNode(hasTestTag("DropDownMenu")).performClick()
            Games.orderedSubclasses.forEach { _ ->
                onNode(hasTestTag("DropDownMenu Item")).assertIsDisplayed()
            }
        }
    }

    @Test
    fun statsMenu_switchGames() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    var selectedGame by remember { mutableStateOf<Games>(KlondikeTurnOne) }
                    var stats by remember { mutableStateOf(gameOneStats) }
                    StatsMenu(
                        selectedGame = selectedGame,
                        updateSelectedGame = {
                            selectedGame = it
                            stats = if (stats == gameOneStats) {
                                gameTwoStats
                            } else {
                                gameOneStats
                            }
                        },
                        selectedGamePersonalStats = stats,
                        selectedGameGlobalStats = gameTwoStats
                    ) { }
                }
            }

            // switch to Australian Patience and check stats
            onNode(hasTestTag("DropDownMenu")).performClick()
            onNode(hasTestTag("DropDownMenu Item")).performClick()
            onNodeWithTextId(AustralianPatience.nameId).assertIsDisplayed()
            gameTwoStatsDisplayed()

            // switch to Alaska and check stats
            onNode(hasTestTag("DropDownMenu")).performClick()
            onNode(hasTestTag("DropDownMenu Item")).performClick()
            onNodeWithTextId(Alaska.nameId).assertIsDisplayed()
            gameOneStatsDisplayed()
        }
    }

    /**
     *  Checks that stats for [gameOneStats] are displayed.
     */
    private fun gameOneStatsDisplayed() {
        composeRule.apply {
            onNode(hasTestTag("Games Played: 10")).assertIsDisplayed()
            onNode(hasTestTag("Games Won: 5 (50%)")).assertIsDisplayed()
            onNode(hasTestTag("Lowest Moves in Win: 30")).assertIsDisplayed()
            onNode(hasTestTag("Average Moves: 10")).assertIsDisplayed()
            onNode(hasTestTag("Total Moves: 100")).assertIsDisplayed()
            onNode(hasTestTag("Fastest Win: 11 minutes, 40 seconds")).assertIsDisplayed()
            onNode(hasTestTag("Average Time: 16 minutes, 40 seconds")).assertIsDisplayed()
            onNode(hasTestTag("Total Time Played: 2 hours, 46 minutes, 40 seconds"))
                .assertIsDisplayed()
            onNode(hasTestTag("Average Score: 20 of 52 (38.46%)")).assertIsDisplayed()
            onNode(hasTestTag("Best Total Score: 329")).assertIsDisplayed()
        }
    }

    /**
     *  Checks that stats for [gameTwoStats] are displayed.
     */
    private fun gameTwoStatsDisplayed() {
        composeRule.apply {
            onNode(hasTestTag("Games Played: 0")).assertIsDisplayed()
            onNode(hasTestTag("Games Won: 0 (0%)")).assertIsDisplayed()
            onNode(hasTestTag("Lowest Moves in Win: 9999")).assertIsDisplayed()
            onNode(hasTestTag("Average Moves: 0")).assertIsDisplayed()
            onNode(hasTestTag("Total Moves: 0")).assertIsDisplayed()
            onNode(hasTestTag("Fastest Win: 99 hours, 59 minutes, 59 seconds")).assertIsDisplayed()
            onNode(hasTestTag("Average Time: 0 minutes, 0 seconds")).assertIsDisplayed()
            onNode(hasTestTag("Total Time Played: 0 minutes, 0 seconds")).assertIsDisplayed()
            onNode(hasTestTag("Average Score: 0 of 52 (0%)")).assertIsDisplayed()
            onNode(hasTestTag("Best Total Score: 99999")).assertIsDisplayed()
        }
    }
}