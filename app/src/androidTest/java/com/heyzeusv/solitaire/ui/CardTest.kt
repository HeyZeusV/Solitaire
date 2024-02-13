package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.onNodeWithConDescId
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
                    card = tc.card1C,
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
                        card = tc.card1DFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card7HFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card12CFU,
                        modifier = modifier.weight(1f)
                    )
                    SolitaireCard(
                        card = tc.card5SFU,
                        modifier = modifier.weight(1f)
                    )
                }
            }
        }

        // no card backs should exist
        composeRule.onNodeWithConDescId(R.string.card_cdesc_back).assertDoesNotExist()
        // 0 of Diamonds
        composeRule.onNode(hasTestTag("A of DIAMONDS")).assertIsDisplayed()
        // 7 of Hearts
        composeRule.onNode(hasTestTag("7 of HEARTS")).assertIsDisplayed()

        // Queen of Clubs
        composeRule.onNode(hasTestTag("Q of CLUBS")).assertIsDisplayed()
        // 5 of Spades
        composeRule.onNode(hasTestTag("5 of SPADES")).assertIsDisplayed()
    }
}