package com.heyzeusv.solitaire.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.heyzeusv.solitaire.MainActivity
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.MenuState
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.clickOnPileTT
import com.heyzeusv.solitaire.util.onNodeWithConDescId
import com.heyzeusv.solitaire.util.onNodeWithTextId
import com.heyzeusv.solitaire.util.waitUntilPileCardExists
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 *  Tests that are not tied to any game.
 */
@HiltAndroidTest
class AppTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val tc = TestCards

    @Test
    fun app_openCloseMenuButtons() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()

            onNodeWithTextId(MenuState.Games.nameId).assertIsDisplayed()
            onNodeWithTextId(MenuState.Stats.nameId).assertIsDisplayed()
            onNodeWithTextId(MenuState.About.nameId).assertIsDisplayed()

            onNodeWithTextId(R.string.tools_button_menu).performClick()

            onNodeWithTextId(MenuState.Games.nameId).assertDoesNotExist()
            onNodeWithTextId(MenuState.Stats.nameId).assertDoesNotExist()
            onNodeWithTextId(MenuState.About.nameId).assertDoesNotExist()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun app_openCloseMenuScreens() {
        composeRule.apply {
            onNodeWithTextId(R.string.tools_button_menu).performClick()

            onNodeWithTextId(MenuState.Games.nameId).performClick()
            onNode(hasTestTag("Games Menu")).assertIsDisplayed()
            onNodeWithConDescId(R.string.menu_cdesc_close, "Games").performClick()
            waitUntilDoesNotExist(hasTestTag("Games Menu"), timeoutMillis = 5000L)

            onNodeWithTextId(MenuState.Stats.nameId).performClick()
            onNode(hasTestTag("Stats Menu")).assertIsDisplayed()
            onNodeWithConDescId(R.string.menu_cdesc_close, "Stats").performClick()
            waitUntilDoesNotExist(hasTestTag("Stats Menu"), timeoutMillis = 5000L)

            onNodeWithTextId(MenuState.About.nameId).performClick()
            onNode(hasTestTag("About Menu")).assertIsDisplayed()
            onNodeWithConDescId(R.string.menu_cdesc_close, "About").performClick()
            waitUntilDoesNotExist(hasTestTag("About Menu"), timeoutMillis = 5000L)
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

    @Test
    fun app_reset_dismiss() {
        composeRule.apply {
            clickOnPileTT("Stock")
            waitUntilPileCardExists("Waste", tc.card2SFU)

            onNodeWithTextId(R.string.tools_button_reset).performClick()

            // check alert dialog appears and select new
            onNodeWithTextId(R.string.reset_ad_title).assertIsDisplayed()
            onNodeWithTextId(R.string.reset_ad_dismiss).performClick()

            onNodeWithTextId(R.string.reset_ad_title).assertDoesNotExist()

            // check that nothing has changed
            waitUntilPileCardExists("Waste", tc.card2SFU)
        }
    }
}