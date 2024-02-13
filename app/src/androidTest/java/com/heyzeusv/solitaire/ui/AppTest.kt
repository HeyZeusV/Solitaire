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
        // checking all piles are displayed
        composeRule.onNode(hasTestTag("Stock")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("Waste")).assertIsDisplayed()
        for (i in 0..3) {
            composeRule.onNode(hasTestTag("Foundation #$i")).assertIsDisplayed()
        }
        for (j in 0..6) {
            composeRule.onNode(hasTestTag("Tableau #$j")).assertIsDisplayed()
        }
        // check that scoreboard is displayed
        composeRule.onNode(hasTestTag("Scoreboard")).assertIsDisplayed()
        // check that tools is displayed and that Undo is disabled
        composeRule.onNode(hasTestTag("Tools")).assertIsDisplayed()
        composeRule.onNodeWithTextId(R.string.tools_button_undo).assertIsNotEnabled()
    }

    @Test
    fun app_onStockClick() {
        composeRule.onNode(hasTestTag("Stock")).performClick()

        composeRule.onNode(hasTestTag("Waste") and hasAnyChild(hasTestTag("2 of SPADES")))
            .assertIsDisplayed()
    }

    @Test
    fun app_onWasteClick() {
        composeRule.apply {
            Thread.sleep(1000)
            onNode(hasTestTag("Stock")).performClick()
            waitUntilPileCardExists("Waste", tc.card2SFU)

            Thread.sleep(1000)
            onNode(hasTestTag("Stock")).performClick()
            waitUntilPileCardExists("Waste", tc.card3DFU)

            Thread.sleep(1000)
            onNode(hasTestTag("Stock")).performClick()
            waitUntilPileCardExists("Waste", tc.card2DFU)

            Thread.sleep(1000)
            onNode(hasTestTag("Waste")).performClick()
            waitUntilPileCardExists("Tableau #3", tc.card2DFU)
            waitUntilPileCardExists("Waste", tc.card3DFU)
        }
    }

    @Test
    fun app_openMenu() {
        composeRule.onNodeWithTextId(R.string.tools_button_menu).performClick()

        composeRule.onNode(hasTestTag("Menu")).assertIsDisplayed()
    }

    @Test
    fun app_closeMenu() {
        composeRule.onNodeWithTextId(R.string.tools_button_menu).performClick()

        // close by clicking outside Card bounds
        composeRule.onNode(hasTestTag("Close Menu")).performClickAt(Offset.Zero)
        composeRule.onNode(hasTestTag("Menu")).assertIsNotDisplayed()

        composeRule.onNodeWithTextId(R.string.tools_button_menu).performClick()

        // close by pressing back button
        Espresso.pressBack()
        composeRule.onNode(hasTestTag("Menu")).assertIsNotDisplayed()
    }

    @Test
    fun app_closeApp_confirm() {
        Espresso.pressBack()

        // check alert dialog appears and confirm closure
        composeRule.onNodeWithTextId(R.string.close_ad_title).assertIsDisplayed()
        composeRule.onNodeWithTextId(R.string.close_ad_confirm).performClick()

        Assert.assertTrue(composeRule.activity.isFinishing)
    }

    @Test
    fun app_closeApp_dismiss() {
        Espresso.pressBack()

        // check alert dialog appears and dismiss closure
        composeRule.onNodeWithTextId(R.string.close_ad_title).assertIsDisplayed()
        composeRule.onNodeWithTextId(R.string.close_ad_dismiss).performClick()

        composeRule.onNodeWithTextId(R.string.close_ad_title).assertIsNotDisplayed()
        Assert.assertTrue(!composeRule.activity.isFinishing)
    }
}