package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
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
class PileTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<ComponentActivity>()

    private val tc = TestCards

    @Test
    fun pile_empty() {
        composeRule.setContent {
            SolitaireTheme {
                SolitairePile(
                    pile = emptyList(),
                    emptyIconId = R.drawable.tableau_empty,
                    cardWidth = 70.dp
                )
            }
        }

        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty).isDisplayed()
        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty).assertHasClickAction()
    }

    // this test would be the same for pile_drawThree_OneCard()
    @Test
    fun pile_drawOne() {
        composeRule.setContent {
            SolitaireTheme {
                SolitairePile(
                    pile = listOf(tc.card3DFU, tc.card6SFU),
                    emptyIconId = R.drawable.tableau_empty,
                    cardWidth = 70.dp
                )
            }
        }

        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

        composeRule.onNode(hasTestTag("7 of SPADES")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("7 of SPADES")).assertHasClickAction()
    }

    @Test
    fun pile_drawThree_ThreeCards() {
        composeRule.setContent {
            SolitaireTheme {
                SolitairePile(
                    pile = listOf(tc.card0CFU, tc.card3DFU, tc.card6SFU),
                    emptyIconId = R.drawable.tableau_empty,
                    drawAmount = 3,
                    cardWidth = 70.dp
                )
            }
        }

        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

        composeRule.onNode(hasTestTag("7 of SPADES")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("7 of SPADES")).assertHasClickAction()

        composeRule.onNode(hasTestTag("4 of DIAMONDS")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("4 of DIAMONDS")).assertHasNoClickAction()

        composeRule.onNode(hasTestTag("A of CLUBS")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("A of CLUBS")).assertHasNoClickAction()
    }

    @Test
    fun pile_drawThree_TwoCards() {
        composeRule.setContent {
            SolitaireTheme {
                SolitairePile(
                    pile = listOf(tc.card3DFU, tc.card6SFU),
                    emptyIconId = R.drawable.tableau_empty,
                    drawAmount = 3,
                    cardWidth = 70.dp
                )
            }
        }

        composeRule.onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

        composeRule.onNode(hasTestTag("7 of SPADES")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("7 of SPADES")).assertHasClickAction()

        composeRule.onNode(hasTestTag("4 of DIAMONDS")).assertIsDisplayed()
        composeRule.onNode(hasTestTag("4 of DIAMONDS")).assertHasNoClickAction()
    }
}