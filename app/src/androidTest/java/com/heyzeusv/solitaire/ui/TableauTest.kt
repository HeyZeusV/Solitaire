package com.heyzeusv.solitaire.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.TestCards
import com.heyzeusv.solitaire.util.onCard
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
        composeRule.apply {
            setContent {
                SolitaireTheme {
                    SolitaireTableau(
                        cardHeight = 100.dp,
                        pile = listOf(
                            tc.card5SFU, tc.card4DFU, tc.card3DFU, tc.card13DFU,
                            tc.card11HFU, tc.card9CFU, tc.card7SFU
                        )
                    )
                }
            }

            onCard(tc.card5SFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card4DFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card3DFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card13DFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card11HFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card9CFU)
                .assertIsDisplayed()
                .assertHasClickAction()
            onCard(tc.card7SFU)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }
}