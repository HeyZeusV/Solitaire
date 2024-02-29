package com.heyzeusv.solitaire.ui.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.MainActivity
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.clickOnTableauCard
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.waitUntilTableauDoesNotExist
import com.heyzeusv.solitaire.util.waitUntilTableauExists
import com.heyzeusv.solitaire.util.waitUntilPileCardDoesNotExists
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *  Tests tied to AustralianPatience.
 */
@HiltAndroidTest
class AustralianPatienceTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Before
    fun setUp() {
        switchToAustralianPatience()
    }

    @Test
    fun australianPatience_startUp() {
        resetState()
    }

    @Test
    fun australianPatience_onStockClick() {
        composeRule.apply {
            onNode(hasTestTag("Stock")).performClick()

            onNode(hasTestTag("Waste") and hasAnyChild(hasTestTag("2 of SPADES")))
                .assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_onWasteClick() {
        composeRule.apply {
            val expectedCard = listOf(
                tc.card2SFU, tc.card3DFU, tc.card2DFU, tc.card1CFU,
                tc.card13SFU, tc.card5DFU, tc.card3HFU, tc.card11CFU
            )
            // draw 8 times from stock
            for (i in 0..7) {
                clickOnPileTT("Stock")
                waitUntilPileCardExists("Waste", expectedCard[i])
            }

            // click on waste and check that Card ends in correct pile
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #6", tc.card11CFU)
            waitUntilPileCardExists("Waste", tc.card3HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 9).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_onFoundationClick() {
        composeRule.apply {
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()

            val expectedCard = listOf(
                tc.card2SFU, tc.card3DFU, tc.card2DFU, tc.card1CFU,
                tc.card13SFU, tc.card5DFU, tc.card3HFU, tc.card11CFU
            )
            // draw 8 times from stock
            for (i in 0..7) {
                clickOnPileTT("Stock")
                waitUntilPileCardExists("Waste", expectedCard[i])
            }

            // move from Waste to Tableau #6
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #6", tc.card11CFU)
            // move from Tableau #1 to Tableau #6
            clickOnTableauCard("Tableau #1", tc.card10CFU)
            waitUntilPileCardExists("Tableau #6", tc.card10CFU)
            // move from Tableau #3 to Tableau #6
            clickOnTableauCard("Tableau #3", tc.card9CFU)
            waitUntilPileCardExists("Tableau #6", tc.card9CFU)
            // move from Tableau #6 to Tableau #3
            clickOnTableauCard("Tableau #6", tc.card13DFU)
            waitUntilPileCardExists("Tableau #3", tc.card13DFU)
            // move from Tableau #1 to Foundation #2
            clickOnTableauCard("Tableau #1", tc.card1HFU)
            waitUntilPileCardExists("Foundation #2", tc.card1HFU)
            onNodeWithTextId(R.string.scoreboard_stat_score, 1).assertIsDisplayed()

            // click on foundation and check that card ends in correct pile
            clickOnPileTT("Foundation #2")
            waitUntilPileCardExists("Tableau #6", tc.card1HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 14).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_onTableauClick_moveOneCard() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #4", tc.card11SFU)
            waitUntilPileCardExists("Tableau #5", tc.card11SFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_onTableauClick_moveMultipleCards() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #3", tc.card9CFU)
            waitUntilPileCardExists("Tableau #1", tc.card9CFU)
            waitUntilPileCardExists("Tableau #1", tc.card2HFU)
            waitUntilPileCardExists("Tableau #1", tc.card13DFU)
            waitUntilPileCardExists("Tableau #1", tc.card12HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_undo() {
        composeRule.apply {
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()
            val expectedCard = listOf(
                tc.card2SFU, tc.card3DFU, tc.card2DFU, tc.card1CFU,
                tc.card13SFU, tc.card5DFU, tc.card3HFU, tc.card11CFU
            )
            // draw 8 times from stock
            for (i in 0..7) {
                clickOnPileTT("Stock")
                waitUntilPileCardExists("Waste", expectedCard[i])
            }

            // move from Waste to Tableau #6
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #6", tc.card11CFU)
            // move from Tableau #1 to Tableau #6
            clickOnTableauCard("Tableau #1", tc.card10CFU)
            waitUntilPileCardExists("Tableau #6", tc.card10CFU)
            // move from Tableau #3 to Tableau #6
            clickOnTableauCard("Tableau #3", tc.card9CFU)
            waitUntilPileCardExists("Tableau #6", tc.card9CFU)
            // move from Tableau #6 to Tableau #3
            clickOnTableauCard("Tableau #6", tc.card13DFU)
            waitUntilPileCardExists("Tableau #3", tc.card13DFU)
            // move from Tableau #1 to Foundation #2
            clickOnTableauCard("Tableau #1", tc.card1HFU)
            waitUntilPileCardExists("Foundation #2", tc.card1HFU)

            onNodeWithTextId(R.string.scoreboard_stat_score, 1).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_moves, 13).assertIsDisplayed()

            // undo move to Foundation, should be back at Tableau #1
            onNodeWithTextId(R.string.tools_button_undo).performClick()
            waitUntilPileCardDoesNotExists("Foundation #2", tc.card1HFU)
            waitUntilPileCardExists("Tableau #1", tc.card1HFU)
            onNodeWithTextId(R.string.scoreboard_stat_score, 0).assertIsDisplayed()
            onNodeWithTextId(R.string.scoreboard_stat_moves, 14).assertIsDisplayed()

            // undo move to Tableau #3, should be back at Tableau #6
            onNodeWithTextId(R.string.tools_button_undo).performClick()
            waitUntilPileCardDoesNotExists("Tableau #3", tc.card13DFU)
            waitUntilPileCardExists("Tableau #6", tc.card13DFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 15).assertIsDisplayed()
        }
    }

    @Test
    fun australianPatience_reset_restart() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_reset).performClick()

            // check alert dialog appears and select new
            onNodeWithTextId(R.string.reset_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.reset_ad_confirm_restart).performClick()

            resetState()

            // checking that cards are the same as first shuffle of Random(10L)
            waitUntilTableauExists(
                "Tableau #0", tc.card5HFU, tc.card11DFU, tc.card4DFU, tc.card7CFU
            )
            waitUntilTableauExists(
                "Tableau #1", tc.card9DFU, tc.card7DFU, tc.card1HFU, tc.card10CFU
            )
            waitUntilTableauExists(
                "Tableau #2", tc.card7SFU, tc.card3CFU, tc.card9SFU, tc.card7HFU
            )
            waitUntilTableauExists(
                "Tableau #3", tc.card9CFU, tc.card2HFU, tc.card13DFU, tc.card12HFU
            )
            waitUntilTableauExists(
                "Tableau #4", tc.card11SFU, tc.card8CFU, tc.card4SFU, tc.card10DFU
            )
            waitUntilTableauExists(
                "Tableau #5", tc.card10SFU, tc.card5CFU, tc.card13CFU, tc.card12SFU
            )
            waitUntilTableauExists(
                "Tableau #6", tc.card13HFU, tc.card6DFU, tc.card2CFU, tc.card12CFU
            )
        }
    }

    @Test
    fun australianPatience_reset_new() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_reset).performClick()

            // check alert dialog appears and select new
            onNodeWithTextId(R.string.reset_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.reset_ad_confirm_new).performClick()

            resetState()

            // checking that cards are not the same as first shuffle of Random(10L)
            waitUntilTableauDoesNotExist(
                "Tableau #0", tc.card5HFU, tc.card11DFU, tc.card4DFU, tc.card7CFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #1", tc.card9DFU, tc.card7DFU, tc.card1HFU, tc.card10CFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #2", tc.card7SFU, tc.card3CFU, tc.card9SFU, tc.card7HFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #3", tc.card9CFU, tc.card2HFU, tc.card13DFU, tc.card12HFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #4", tc.card11SFU, tc.card8CFU, tc.card4SFU, tc.card10DFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #5", tc.card10SFU, tc.card5CFU, tc.card13CFU, tc.card12SFU
            )
            waitUntilTableauDoesNotExist(
                "Tableau #6", tc.card13HFU, tc.card6DFU, tc.card2CFU, tc.card12CFU
            )
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
                assert(onNode(hasTestTag("Tableau #$j")).fetchSemanticsNode().children.size == 4)
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

    /**
     *  Switches game to Australian Patience.
     */
    private fun switchToAustralianPatience() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()
            onNodeWithTextId(R.string.games_australian_patience).performClick()

            // close by pressing back button
            Espresso.pressBack()
        }
    }
}