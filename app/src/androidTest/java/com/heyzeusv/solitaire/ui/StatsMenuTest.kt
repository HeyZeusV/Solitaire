package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.tools.StatsMenu
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsMenuTest {

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

    @Test
    fun statsMenu_display() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    StatsMenu(
                        updateMenuState = { },
                        selectedGame = Games.KLONDIKE_TURN_ONE,
                        updateSelectedGame = { },
                        stats = gameOneStats
                    )
                }
            }

            onNodeWithTextId(R.string.menu_button_stats).assertIsDisplayed()
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
}