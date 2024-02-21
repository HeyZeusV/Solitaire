package com.heyzeusv.solitaire.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.clickOnTableauCard
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import com.heyzeusv.solitaire.util.waitUntilTableauExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *  Tests ties to Russian.
 */
@HiltAndroidTest
class RussianTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Before
    fun setUp() {
        switchToRussian()
    }

    /**
     *  Uses same reset() function as Yukon and is tested in [YukonTest].
     */

    @Test
    fun russian_onTableauClick_moveOneCard() {
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
    fun russian_onTableauClick_moveMultipleCards() {
        composeRule.apply {
            // click on tableau and check that cards ends in correct pile
            clickOnTableauCard("Tableau #5", tc.card4CFU)
            waitUntilTableauExists("Tableau #3", tc.card5CFU, tc.card4CFU, tc.card11HFU, tc.card6HFU)
            onNodeWithTextId(R.string.scoreboard_stat_moves, 1).assertIsDisplayed()
        }
    }

    /**
     *  Switches game to Yukon.
     */
    private fun switchToRussian() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()
            onNodeWithTextId(R.string.games_russian).performClick()

            // close by pressing back button
            Espresso.pressBack()
        }
    }
}