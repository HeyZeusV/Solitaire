package com.heyzeusv.solitaire.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.clickOnTableauCard
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.performClickAt
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AppTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Test
    fun app_startUp() {
        composeRule.apply {
            // checking all piles are displayed
            onNode(hasTestTag("Stock")).assertIsDisplayed()
            onNode(hasTestTag("Waste")).assertIsDisplayed()
            for (i in 0..3) {
                onNode(hasTestTag("Foundation #$i")).assertIsDisplayed()
            }
            for (j in 0..6) {
                onNode(hasTestTag("Tableau #$j")).assertIsDisplayed()
            }
            // check that scoreboard is displayed
            onNode(hasTestTag("Scoreboard")).assertIsDisplayed()
            // check that tools is displayed and that Undo is disabled
            onNode(hasTestTag("Tools")).assertIsDisplayed()
            onNodeWithTextId(R.string.tools_button_undo).assertIsNotEnabled()
        }
    }

    @Test
    fun app_onStockClick() {
        composeRule.apply {
            onNode(hasTestTag("Stock")).performClick()

            onNode(hasTestTag("Waste") and hasAnyChild(hasTestTag("2 of SPADES")))
                .assertIsDisplayed()
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
        }
    }

    @Test
    fun app_onFoundationClick() {
        composeRule.apply {
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
            // card should move to tableau
            clickOnPileTT("Waste")
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)

            // click on foundation and check that card ends in correct pile
            clickOnPileTT("Foundation #0")
            waitUntilPileCardExists("Tableau #3", tc.card1CFU)
        }
    }

    @Test
    fun app_onTableauClick_moveOneCard() {
        composeRule.apply {
            // click on tableau and check that card ends in correct pile
            clickOnTableauCard("Tableau #3", tc.card3CFU)
            waitUntilPileCardExists("Tableau #1", tc.card3CFU)
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
        }
    }

    @Test
    fun app_openMenu() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()

            onNode(hasTestTag("Menu")).assertIsDisplayed()
        }
    }

    @Test
    fun app_closeMenu() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()

            // close by clicking outside Card bounds
            onNode(hasTestTag("Close Menu")).performClickAt(Offset.Zero)
            onNode(hasTestTag("Menu")).assertIsNotDisplayed()

            onNodeWithTextId(R.string.tools_button_menu).performClick()

            // close by pressing back button
            Espresso.pressBack()
            onNode(hasTestTag("Menu")).assertIsNotDisplayed()
        }
    }

    @Test
    fun app_closeApp_confirm() {
        composeRule.apply {
            Espresso.pressBack()

            // check alert dialog appears and confirm closure
            onNodeWithTextId(R.string.close_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.close_ad_confirm).performClick()

            Assert.assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun app_closeApp_dismiss() {
        composeRule.apply {
            Espresso.pressBack()

            // check alert dialog appears and dismiss closure
            onNodeWithTextId(R.string.close_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.close_ad_dismiss).performClick()

            onNodeWithTextId(R.string.close_ad_title).assertIsNotDisplayed()
            Assert.assertTrue(!activity.isFinishing)
        }
    }
}