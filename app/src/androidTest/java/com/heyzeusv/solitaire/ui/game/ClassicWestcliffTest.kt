package com.heyzeusv.solitaire.ui.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.MainActivity
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.switchGame
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *  Tests tied to Classic Westcliff.
 */
@HiltAndroidTest
class ClassicWestcliffTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeRule.switchGame(Games.CLASSIC_WESTCLIFF)
    }

    @Test
    fun classicWestcliff_reset() {
        resetState()
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
                assert(onNode(hasTestTag("Foundation #$i")).fetchSemanticsNode().children.size == 1)
            }
            for (j in 0..6) {
                onNode(hasTestTag("Tableau #$j")).assertIsDisplayed()
                assert(onNode(hasTestTag("Tableau #$j")).fetchSemanticsNode().children.size == 3)
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