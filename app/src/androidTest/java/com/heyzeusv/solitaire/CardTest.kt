package com.heyzeusv.solitaire

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.ui.SolitaireCard
import com.heyzeusv.solitaire.util.SolitairePreview
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private val modifier = Modifier.height(200.dp)
    private val tc = TestCards

    @Test
    fun card_faceDown() {
        composeRule.setContent {
            SolitairePreview {
                SolitaireCard(
                    card = tc.card0C,
                    modifier = modifier
                )
            }
        }

        composeRule.onNodeWithConDescId(R.string.card_cdesc_back).assertIsDisplayed()
        composeRule.onNodeWithConDescId(R.string.card_cdesc_icon, "A", "Clubs").assertDoesNotExist()
    }

    @Test
    fun card_faceUp() {
        composeRule.setContent {
            SolitairePreview {
                Row {
                    SolitaireCard(
                        card = tc.card0DFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card6HFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card11CFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card4SFU,
                        modifier = modifier.weight(1f)
                    )
                }
            }
        }

        // no card backs should exist
        composeRule.onNodeWithConDescId(R.string.card_cdesc_back).assertDoesNotExist()

        // 0 of Diamonds
        assertEquals(
            2,
            composeRule.onAllNodesWithConDescId(R.string.card_cdesc_icon, "A", "Diamonds")
                .fetchSemanticsNodes().size
        )
        composeRule.onNodeWithText("A").assertIsDisplayed()
        // 7 of Hearts
        assertEquals(
            2,
            composeRule.onAllNodesWithConDescId(R.string.card_cdesc_icon, "7", "Hearts")
                .fetchSemanticsNodes().size
        )
        composeRule.onNodeWithText("7").assertIsDisplayed()
        // Queen of Clubs
        assertEquals(
            2,
            composeRule.onAllNodesWithConDescId(R.string.card_cdesc_icon, "Q", "Clubs")
                .fetchSemanticsNodes().size
        )
        composeRule.onNodeWithText("Q").assertIsDisplayed()
        // 5 of Spades
        assertEquals(
            2,
            composeRule.onAllNodesWithConDescId(R.string.card_cdesc_icon, "5", "Spades")
                .fetchSemanticsNodes().size
        )
        composeRule.onNodeWithText("5").assertIsDisplayed()
    }
}