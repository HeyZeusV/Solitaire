package com.heyzeusv.solitaire.ui.game

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.SolitaireCard
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.onCard
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
        composeRule.apply {
            setContent {
                SolitairePreview {
                    SolitaireCard(
                        card = tc.card1C,
                        modifier = modifier
                    )
                }
            }

            onNodeWithConDescId(R.string.card_cdesc_back).assertIsDisplayed()
            onNodeWithConDescId(R.string.card_cdesc_icon, "A", "Clubs").assertDoesNotExist()
        }
    }

    @Test
    fun card_faceUp() {
        composeRule.apply {
            setContent {
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
            onNodeWithConDescId(R.string.card_cdesc_back).assertDoesNotExist()
            // 0 of Diamonds
            onCard(tc.card1DFU).assertIsDisplayed()
            // 7 of Hearts
            onCard(tc.card7HFU).assertIsDisplayed()
            // Queen of Clubs
            onCard(tc.card12CFU).assertIsDisplayed()
            // 5 of Spades
            onCard(tc.card5SFU).assertIsDisplayed()
        }
    }
}