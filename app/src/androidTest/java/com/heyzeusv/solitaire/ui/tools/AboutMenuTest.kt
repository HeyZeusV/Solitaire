package com.heyzeusv.solitaire.ui.tools

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.ui.toolbar.AboutMenu
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutMenuTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeRule.setContent {
            SolitaireTheme {
                AboutMenu { }
            }
        }
    }

    @Test
    fun aboutMenu_display() {
        composeRule.apply {
            onNodeWithText("CHANGELOG").assertIsDisplayed().assertIsEnabled()
            onNode(hasTestTag("About Changelog.txt")).assertDoesNotExist()
        }
    }

    @Test
    fun aboutMenu_ButtonRevealContent_show_hide() {
        composeRule.apply {
            onNodeWithText("CHANGELOG").performClick()
            onNode(hasTestTag("About Changelog.txt")).assertIsDisplayed()

            onNodeWithText("CHANGELOG").performClick()
            onNode(hasTestTag("About Changelog.txt")).assertDoesNotExist()
        }
    }
}