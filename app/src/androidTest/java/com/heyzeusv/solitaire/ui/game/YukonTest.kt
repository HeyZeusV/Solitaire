package com.heyzeusv.solitaire.ui.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.MainActivity
import com.heyzeusv.solitaire.util.TestCards
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
 *  Tests tied to Yukon.
 */
@HiltAndroidTest
class YukonTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Before
    fun setUp() {
//        composeRule.switchGame(Games.YUKON)
    }

    @Test
    fun yukon_startUp() {
        resetState()
    }

    @Test
    fun yukon_onTableauClick_moveOneCard() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #2", tc.card2HFU)
            waitUntilPileCardExists("Tableau #6", tc.card2HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    @Test
    fun yukon_onTableauClick_moveMultipleCards() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #1", tc.card4DFU)
            waitUntilTableauExists("Tableau #3", tc.card10DFU, tc.card10SFU, tc.card5CFU, tc.card4DFU)
            waitUntilTableauExists("Tableau #3", tc.card7CFU, tc.card9DFU, tc.card7DFU, tc.card1HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
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
            assert(onNode(hasTestTag("Stock")).fetchSemanticsNode().children.isEmpty())
            onNode(hasTestTag("Waste")).assertIsDisplayed()
            assert(onNode(hasTestTag("Waste")).fetchSemanticsNode().children.isEmpty())
            for (i in 0..3) {
                onNode(hasTestTag("Foundation #$i")).assertIsDisplayed()
                assert(onNode(hasTestTag("Foundation #$i")).fetchSemanticsNode().children.isEmpty())
            }
            for (j in 0..6) {
                onNode(hasTestTag("Tableau #$j")).assertIsDisplayed()
                if (j == 0) {
                    assert(onNode(hasTestTag("Tableau #$j")).fetchSemanticsNode().children.size == 1)
                } else {
                    assert(onNode(hasTestTag("Tableau #$j")).fetchSemanticsNode().children.size == 5 + j)
                }
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