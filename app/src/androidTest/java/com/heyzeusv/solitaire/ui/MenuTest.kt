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
        composeRule.apply {
            setContent {
                SolitaireTheme(darkTheme = true) {
                    SolitaireMenu(
                        displayMenu = true,
                        updateDisplayMenu = { },
                        lgs = LastGameStats(false, 0, 0L, 0),
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        updateSelectedGame = { },
                        updateStats = { },
                        reset = { },
                        stats = gameOneStats
                    )
                }
            }

            // selectable games/filter chips
            onNodeWithTextId(R.string.games_klondike_turn_one).assertIsSelected()
            onNodeWithTextId(R.string.games_klondike_turn_three).assertIsNotSelected()
            // stats
            onNodeWithTextId(
                R.string.menu_content_stats,
                10, // games played
                5, // games won
                50, // win percentage
                30, // lowest moves
                10, // average moves
                100, // total moves
                "11 minutes, 40 seconds", // fastest win
                "16 minutes, 40 seconds", // average time
                "2 hours, 46 minutes, 40 seconds", // total time
                20, // average score
                38.46, // score percentage
                329 // best total score
            ).assertIsDisplayed()
        }
    }

    @Test
    fun menu_switchGames() {
        composeRule.apply {
            setContent {
                SolitaireTheme(darkTheme = true) {
                    var selectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_ONE) }
                    var stats by remember { mutableStateOf(gameOneStats) }
                    SolitaireMenu(
                        displayMenu = true,
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
            onNodeWithTextId(R.string.games_klondike_turn_three).performClick()
            onNodeWithTextId(R.string.games_klondike_turn_three).assertIsSelected()
            onNodeWithTextId(R.string.games_klondike_turn_one).assertIsNotSelected()

            // check that stats switched
            onNodeWithTextId(
                R.string.menu_content_stats,
                0, // games played
                0, // games won
                0, // win percentage
                9999, // lowest moves
                0, // average moves
                0, // total moves
                "99 hours, 59 minutes, 59 seconds", // fastest win
                "0 minutes, 0 seconds", // average time
                "0 minutes, 0 seconds", // total time
                0, // average score
                0, // score percentage
                99999 // best total score
            ).assertIsDisplayed()
        }
    }

    @Test
    fun menu_switchGames_midGame_confirm() {
        composeRule.apply {
            setContent {
                SolitaireTheme(darkTheme = true) {
                    var selectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_ONE) }
                    var stats by remember { mutableStateOf(gameOneStats) }
                    SolitaireMenu(
                        displayMenu = true,
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
            onNodeWithTextId(R.string.games_klondike_turn_three).performClick()

            // check that AlertDialog appears
            onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
            // confirm game switch
            onNodeWithTextId(R.string.games_ad_confirm).performClick()

            // check that game did switch
            onNodeWithTextId(R.string.games_klondike_turn_three).assertIsSelected()
            onNodeWithTextId(R.string.games_klondike_turn_one).assertIsNotSelected()
            onNodeWithTextId(
                R.string.menu_content_stats,
                0, // games played
                0, // games won
                0, // win percentage
                9999, // lowest moves
                0, // average moves
                0, // total moves
                "99 hours, 59 minutes, 59 seconds", // fastest win
                "0 minutes, 0 seconds", // average time
                "0 minutes, 0 seconds", // total time
                0, // average score
                0, // score percentage
                99999 // best total score
            ).assertIsDisplayed()
        }
    }

    @Test
    fun menu_switchGames_midGame_dismiss() {
        composeRule.apply {
            setContent {
                SolitaireTheme(darkTheme = true) {
                    var selectedGame by remember { mutableStateOf(Games.KLONDIKE_TURN_ONE) }
                    var stats by remember { mutableStateOf(gameOneStats) }
                    SolitaireMenu(
                        displayMenu = true,
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
            onNodeWithTextId(R.string.games_klondike_turn_three).performClick()

            // check that AlertDialog appears
            onNodeWithTextId(R.string.games_ad_title).assertIsDisplayed()
            // dismiss game switch
            onNodeWithTextId(R.string.games_ad_dismiss).performClick()

            // check that game did not switch
            onNodeWithTextId(R.string.games_klondike_turn_one).assertIsSelected()
            onNodeWithTextId(R.string.games_klondike_turn_three).assertIsNotSelected()
            onNodeWithTextId(
                R.string.menu_content_stats,
                10, // games played
                5, // games won
                50, // win percentage
                30, // lowest moves
                10, // average moves
                100, // total moves
                "11 minutes, 40 seconds", // fastest win
                "16 minutes, 40 seconds", // average time
                "2 hours, 46 minutes, 40 seconds", // total time
                20, // average score
                38.46, // score percentage
                329 // best total score
            ).assertIsDisplayed()
        }
    }
}