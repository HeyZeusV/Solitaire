package com.heyzeusv.solitaire.ui.game

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.onCard
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
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    Pile(
                        pile = emptyList(),
                        emptyIconId = R.drawable.tableau_empty,
                        cardDpSize = DpSize(100.dp, 140.dp)
                    )
                }
            }

            onNodeWithConDescId(R.string.pile_cdesc_empty).isDisplayed()
            onNodeWithConDescId(R.string.pile_cdesc_empty).assertHasClickAction()
        }
    }

    // this test would be the same for pile_drawThree_OneCard()
    @Test
    fun pile_drawOne() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    Pile(
                        pile = listOf(tc.card4DFU, tc.card7SFU),
                        emptyIconId = R.drawable.tableau_empty,
                        cardDpSize = DpSize(100.dp, 140.dp)
                    )
                }
            }

            onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

            onCard(tc.card7SFU)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun pile_drawThree_ThreeCards() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    Pile(
                        pile = listOf(tc.card1CFU, tc.card4DFU, tc.card7SFU),
                        emptyIconId = R.drawable.tableau_empty,
                        drawAmount = DrawAmount.Three,
                        cardDpSize = DpSize(100.dp, 140.dp)
                    )
                }
            }

            onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

            onCard(tc.card7SFU)
                .assertIsDisplayed()
                .assertHasClickAction()

            onCard(tc.card4DFU)
                .assertIsDisplayed()
                .assertHasNoClickAction()

            onCard(tc.card1CFU)
                .assertIsDisplayed()
                .assertHasNoClickAction()
        }
    }

    @Test
    fun pile_drawThree_TwoCards() {
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    Pile(
                        pile = listOf(tc.card4DFU, tc.card7SFU),
                        emptyIconId = R.drawable.tableau_empty,
                        drawAmount = DrawAmount.Three,
                        cardDpSize = DpSize(100.dp, 140.dp)
                    )
                }
            }

            onNodeWithConDescId(R.string.pile_cdesc_empty).assertDoesNotExist()

            onCard(tc.card7SFU)
                .assertIsDisplayed()
                .assertHasClickAction()

            onCard(tc.card4DFU)
                .assertIsDisplayed()
                .assertHasNoClickAction()
        }
    }
}