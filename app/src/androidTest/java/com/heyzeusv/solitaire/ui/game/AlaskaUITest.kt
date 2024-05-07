package com.heyzeusv.solitaire.ui.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.heyzeusv.solitaire.MainActivity
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.clickOnTableauCard
import com.heyzeusv.solitaire.util.onNodeWithTextId
//import com.heyzeusv.solitaire.util.switchGame
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import com.heyzeusv.solitaire.util.waitUntilTableauExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *  Tests ties to Alaska.
 */
@HiltAndroidTest
class AlaskaUITest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Before
    fun setUp() {
//        composeRule.switchGame(Games.ALASKA)
    }

    /**
     *  Uses same reset() function as Yukon and is tested in [YukonTest].
     */

    @Test
    fun alaska_onTableauClick_moveOneCardDesc() {
        composeRule.apply {
            // click on tableau and check that cards ends in correct pile
            clickOnTableauCard("Tableau #1", tc.card1HFU)
            waitUntilPileCardExists("Foundation #2", tc.card1HFU)
            clickOnPileTT("Foundation #2")
            waitUntilPileCardExists("Tableau #2", tc.card1HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 2).assertIsDisplayed()
        }
    }

    @Test
    fun alaska_onTableauClick_moveOneCardAsc() {
        composeRule.apply {
            // click on tableau and check that cards ends in correct pile
            clickOnTableauCard("Tableau #2", tc.card2HFU)
            waitUntilPileCardExists("Tableau #1", tc.card2HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun alaska_onTableauClick_moveMultipleCardsDesc() {
        composeRule.apply {
            // click on tableau and check that cards ends in correct pile
            clickOnTableauCard("Tableau #5", tc.card4CFU)
            waitUntilTableauExists("Tableau #3", tc.card5CFU, tc.card4CFU, tc.card11HFU, tc.card6HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun alaska_onTableauClick_moveMultipleCardsAsc() {
        composeRule.apply {
            // click on tableau and check that cards ends in correct pile
            clickOnTableauCard("Tableau #3", tc.card4SFU)
            waitUntilTableauExists("Tableau #6", tc.card3SFU, tc.card4SFU, tc.card10DFU, tc.card10SFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }
}