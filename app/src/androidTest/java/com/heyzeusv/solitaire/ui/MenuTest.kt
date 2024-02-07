package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.onNodeWithTextId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private var gameOneStats = getStatsDefaultInstance().toBuilder()
        .setGamesPlayed(10)
        .setGamesWon(5)
        .setLowestMoves(30)
        .setTotalMoves(100)
        .setFastestWin(700L)
        .setTotalTime(10000L)
        .setTotalScore(200)
        .setBestTotalScore(329L)
        .build()

    private var gameTwoStats = getStatsDefaultInstance()

    @Test
    fun menu_display() {
        composeRule.setContent {
            SolitaireTheme(darkTheme = true) {
                SolitaireMenu(
                    updateDisplayMenu = { },
                    lgs = LastGameStats(false, 0, 0L, 0),
                    selectedGame = Games.KLONDIKETURNONE,
                    updateSelectedGame = { },
                    updateStats = { },
                    reset = { },
                    stats = gameOneStats
                )
            }
        }

        // selectable games/filter chips
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_one).assertIsSelected()
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).assertIsNotSelected()
        // stats
        composeRule.onNodeWithTextId(
            R.string.menu_content_stats,
            10, 5, 50, 30, 10, 100,
            "11 minutes, 40 seconds", "16 minutes, 40 seconds", "2 hours, 46 minutes, 40 seconds",
            20, 38.46, 329
        ).assertIsDisplayed()
    }

    @Test
    fun menu_switchGames() {
        composeRule.setContent {
            SolitaireTheme(darkTheme = true) {
                var selectedGame by remember { mutableStateOf(Games.KLONDIKETURNONE) }
                var stats by remember { mutableStateOf(gameOneStats) }
                SolitaireMenu(
                    updateDisplayMenu = { },
                    lgs = LastGameStats(false, 0, 0L, 0),
                    selectedGame = selectedGame,
                    updateSelectedGame = {
                        selectedGame = it
                        stats = gameTwoStats
                    },
                    updateStats = { },
                    reset = { },
                    stats = stats
                )
            }
        }

        // switch games
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).performClick()
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).assertIsSelected()
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_one).assertIsNotSelected()

        // check that stats switched
        composeRule.onNodeWithTextId(
            R.string.menu_content_stats,
            0, 0, 0, 9999, 0, 0,
            "99 hours, 59 minutes, 59 seconds", "0 minutes, 0 seconds", "0 minutes, 0 seconds",
            0, 0, 99999
        ).assertIsDisplayed()
    }

    @Test
    fun menu_switchGames_midGame_confirm() {
        composeRule.setContent {
            SolitaireTheme(darkTheme = true) {
                var selectedGame by remember { mutableStateOf(Games.KLONDIKETURNONE) }
                var stats by remember { mutableStateOf(gameOneStats) }
                SolitaireMenu(
                    updateDisplayMenu = { },
                    lgs = LastGameStats(false, 10, 100L, 2),
                    selectedGame = selectedGame,
                    updateSelectedGame = {
                        selectedGame = it
                        stats = gameTwoStats
                    },
                    updateStats = { },
                    reset = { },
                    stats = stats
                )
            }
        }

        // try to switch games
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).performClick()

        // check that AlertDialog appears
        composeRule.onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
        // confirm game switch
        composeRule.onNodeWithTextId(R.string.games_ad_confirm).performClick()

        // check that game did switch
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).assertIsSelected()
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_one).assertIsNotSelected()
        composeRule.onNodeWithTextId(
            R.string.menu_content_stats,
            0, 0, 0, 9999, 0, 0,
            "99 hours, 59 minutes, 59 seconds", "0 minutes, 0 seconds", "0 minutes, 0 seconds",
            0, 0, 99999
        ).assertIsDisplayed()
    }

    @Test
    fun menu_switchGames_midGame_dismiss() {
        composeRule.setContent {
            SolitaireTheme(darkTheme = true) {
                var selectedGame by remember { mutableStateOf(Games.KLONDIKETURNONE) }
                var stats by remember { mutableStateOf(gameOneStats) }
                SolitaireMenu(
                    updateDisplayMenu = { },
                    lgs = LastGameStats(false, 10, 100L, 2),
                    selectedGame = selectedGame,
                    updateSelectedGame = {
                        selectedGame = it
                        stats = gameTwoStats
                    },
                    updateStats = { },
                    reset = { },
                    stats = stats
                )
            }
        }

        // try to switch games
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).performClick()

        // check that AlertDialog appears
        composeRule.onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
        // dismiss game switch
        composeRule.onNodeWithTextId(R.string.games_ad_dismiss).performClick()

        // check that game did not switch
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_one).assertIsSelected()
        composeRule.onNodeWithTextId(R.string.games_klondike_turn_three).assertIsNotSelected()
        composeRule.onNodeWithTextId(
            R.string.menu_content_stats,
            10, 5, 50, 30, 10, 100,
            "11 minutes, 40 seconds", "16 minutes, 40 seconds", "2 hours, 46 minutes, 40 seconds",
            20, 38.46, 329
        ).assertIsDisplayed()
    }
}