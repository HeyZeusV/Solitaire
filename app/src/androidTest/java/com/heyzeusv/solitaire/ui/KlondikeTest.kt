package com.heyzeusv.solitaire.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.clickOnTableauCard
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.waitUntilPileCardDoesNotExists
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 *  Tests tied to Klondike games.
 */
@HiltAndroidTest
class KlondikeTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Test
    fun app_startUp() {
        resetState()
    }

    @Test
    fun app_onStockClick() {
        composeRule.apply {
            onNode(hasTestTag("Stock")).performClick()

            onNode(hasTestTag("Waste") and hasAnyChild(hasTestTag("2 of SPADES")))
                .assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1)
        }
    }

    @Test
    fun app_onWasteClick() {
        composeRule.apply {
            // draw 3 times from stock
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2SFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card3DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2DFU)

            // click on waste and check that Card ends in correct pile
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)
            waitUntilPileCardExists("Waste", tc.card3DFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 4)
        }
    }

    @Test
    fun app_onFoundationClick() {
        composeRule.apply {
            onNodeWithTextId(R.string.scoreboard_stat_score, 0)

            // draw from stock 4 times
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2SFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card3DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card1CFU)

            // card should move to foundation
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Foundation #0", tc.card1CFU)
            onNodeWithTextId(R.string.scoreboard_stat_score, 1)
            // card should move to tableau
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)

            // click on foundation and check that card ends in correct pile
            clickOnPileTT("Foundation #0")
            waitUntilPileCardExists("Tableau #3", tc.card1CFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 7)
            onNodeWithTextId(R.string.scoreboard_stat_score, 0)
        }
    }

    @Test
    fun app_onTableauClick_moveOneCard() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #3", tc.card3CFU)
            waitUntilPileCardExists("Tableau #1", tc.card3CFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1)
        }
    }

    @Test
    fun app_onTableauClick_moveMultipleCards() {
        composeRule.apply {
            // draw from stock 3 times
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2SFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card3DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2DFU)

            // click on waste and check that Card ends in correct pile
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)

            // click on tableau and check that both Cards end in the correct pile
            clickOnTableauCard("Tableau #3", tc.card3CFU)
            waitUntilPileCardExists("Tableau #1", tc.card4DFU)
            waitUntilPileCardExists("Tableau #1", tc.card3CFU)
            waitUntilPileCardExists("Tableau #1", tc.card2DFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 5)
        }
    }

    @Test
    fun app_undo() {
        composeRule.apply {
            onNodeWithTextId(R.string.scoreboard_stat_score, 0)

            // draw from stock 4 times
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2SFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card3DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2DFU)
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card1CFU)

            // card should move to foundation
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Foundation #0", tc.card1CFU)
            onNodeWithTextId(R.string.scoreboard_stat_score, 1)
            // card should move to tableau
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 6)

            // undo move to Tableau, should be back at Waste
            onNodeWithTextId(R.string.tools_button_undo).performClick()
            waitUntilPileCardDoesNotExists("Tableau #3", tc.card2DFU)
            waitUntilPileCardExists("Waste", tc.card2DFU)

            // undo move to Foundation, should be back at Waste
            onNodeWithTextId(R.string.tools_button_undo).performClick()
            waitUntilPileCardDoesNotExists("Foundation #0", tc.card1CFU)
            waitUntilPileCardExists("Waste", tc.card1CFU)
            onNodeWithTextId(R.string.scoreboard_stat_score, 0)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 8)
        }
    }

    @Test
    fun app_reset_restart() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_reset).performClick()

            // check alert dialog appears and select new
            onNodeWithTextId(R.string.reset_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.reset_ad_confirm_restart).performClick()

            resetState()

            // checking that cards are the same as first shuffle of Random(10L)
            waitUntilPileCardExists("Tableau #0", tc.card5HFU)
            waitUntilPileCardExists("Tableau #1", tc.card4DFU)
            waitUntilPileCardExists("Tableau #2", tc.card7DFU)
            waitUntilPileCardExists("Tableau #3", tc.card3CFU)
            waitUntilPileCardExists("Tableau #4", tc.card13DFU)
            waitUntilPileCardExists("Tableau #5", tc.card10SFU)
            waitUntilPileCardExists("Tableau #6", tc.card12CFU)
        }
    }

    @Test
    fun app_reset_new() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_reset).performClick()

            // check alert dialog appears and select new
            onNodeWithTextId(R.string.reset_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.reset_ad_confirm_new).performClick()

            resetState()

            // checking that cards are not the same as first shuffle of Random(10L)
            waitUntilPileCardDoesNotExists("Tableau #0", tc.card5HFU)
            waitUntilPileCardDoesNotExists("Tableau #1", tc.card4DFU)
            waitUntilPileCardDoesNotExists("Tableau #2", tc.card7DFU)
            waitUntilPileCardDoesNotExists("Tableau #3", tc.card3CFU)
            waitUntilPileCardDoesNotExists("Tableau #4", tc.card13DFU)
            waitUntilPileCardDoesNotExists("Tableau #5", tc.card10SFU)
            waitUntilPileCardDoesNotExists("Tableau #6", tc.card12CFU)
        }
    }

    /**
     *  Checks that all piles are displayed and have the correct number of children on reset. Also
     *  checks that Scoreboard values are at zero and Undo button is disabled.
     */
    private fun resetState() {
        composeRule.apply {
            // checking all piles are displayed
            onNode(hasTestTag("Stock")).assertIsDisplayed()
            assert(onNode(hasTestTag("Stock")).fetchSemanticsNode().children.size == 1)
            onNode(hasTestTag("Waste")).assertIsDisplayed()
            assert(onNode(hasTestTag("Waste")).fetchSemanticsNode().children.isEmpty())
            for (i in 0..3) {
                onNode(hasTestTag("Foundation #$i")).assertIsDisplayed()
                assert(onNode(hasTestTag("Foundation #$i")).fetchSemanticsNode().children.isEmpty())
            }
            for (j in 0..6) {
                onNode(hasTestTag("Tableau #$j")).assertIsDisplayed()
                assert(onNode(hasTestTag("Tableau #$j")).fetchSemanticsNode().children.size == j + 1)
            }
            // check that scoreboard is displayed
            onNode(hasTestTag("Scoreboard")).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_moves, 0).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_time, "00:00").assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()
            // check that tools is displayed and that Undo is disabled
            onNode(hasTestTag("Tools")).assertIsDisplayed()
            onNodeWithTextId(R.string.tools_button_undo).assertIsNotEnabled()
        }
    }
}