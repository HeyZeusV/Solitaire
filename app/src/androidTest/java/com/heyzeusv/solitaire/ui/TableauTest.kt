package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.onNodeWithConDescId
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TableauTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private val tc = TestCards

    @Test
    fun tableau_empty() {
        composeRule.setContent {
            SolitaireTheme {
                SolitaireTableau(cardHeight = 120.dp)
            }
        }

        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty)
            .assertIsDisplayed()
            .assertHasNoClickAction()
    }

    @Test
    fun tableau_sevenCards() {
        composeRule.setContent {
            SolitaireTheme {
                SolitaireTableau(
                    cardHeight = 100.dp,
                    pile = listOf(
                        tc.card6SFU, tc.card3DFU, tc.card2DFU, tc.card12DFU,
                        tc.card10HFU, tc.card8CFU, tc.card4SFU
                    )
                )
            }
        }

        composeRule.onNode(hasTestTag("5 of SPADES"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("9 of CLUBS"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("J of HEARTS"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("K of DIAMONDS"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("3 of DIAMONDS"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("4 of DIAMONDS"))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule.onNode(hasTestTag("7 of SPADES"))
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}