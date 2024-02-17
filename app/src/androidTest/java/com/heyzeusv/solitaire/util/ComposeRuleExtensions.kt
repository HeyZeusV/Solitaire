package com.heyzeusv.solitaire.util

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.heyzeusv.solitaire.data.Card

/**
 *  Looks for node with text that matches given String resource [id] with [args] (if any).
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTextId(
    @StringRes id: Int,
    vararg args: Any?
): SemanticsNodeInteraction = onNodeWithText((activity.getString(id, *args)))

/**
 *  Looks for node with content description that matches given String resource [id] with [args]
 *  (if any).
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithConDescId(
    @StringRes id: Int,
    vararg args: Any?
): SemanticsNodeInteraction = onNodeWithContentDescription((activity.getString(id, *args)))

/**
 *  Looks for node with test tag that matches given [card] toString().
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onCard(
    card: Card
): SemanticsNodeInteraction = onNode(hasTestTag(card.toString()))

/**
 *  Looks for node with test tag that matches given [pile] and clicks on it.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.clickOnPileTT(
    pile: String
): SemanticsNodeInteraction {
    Thread.sleep(1000)
    return onNode(hasTestTag(pile)).performClick()
}

/**
 *  Looks for given [card] and checks that it belongs to [tableau] pile, then clicks near top
 *  left corner.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.clickOnTableauCard(
    tableau: String,
    card: Card
): SemanticsNodeInteraction {
    Thread.sleep(1000)
    return onNode(hasTestTag(card.toString()) and hasParent(hasTestTag(tableau)))
        .performClickAt(Offset(10f, 5f))
}

/**
 *  Waits until given [card] appears under pile with given [parentTT] test tag. Uses default timeout
 *  of 1 second before failing.
 */
@OptIn(ExperimentalTestApi::class)
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilPileCardExists(
    parentTT: String,
    card: Card
) = waitUntilExactlyOneExists(hasTestTag(parentTT) and hasAnyChild(hasTestTag(card.toString())))

/**
 *  Waits until given [card] disappears under pile with given [parentTT] test tag. Uses default
 *  timeout of 1 second before failing.
 */
@OptIn(ExperimentalTestApi::class)
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilPileCardDoesNotExists(
    parentTT: String,
    card: Card
) = waitUntilDoesNotExist(hasTestTag(parentTT) and hasAnyChild(hasTestTag(card.toString())))

/**
 *  Waits until given [Card]s appear under pile with given [parentTT] test tag. Uses default
 *  timeout of 1 second before failing. Used for testing Australian Patience
 */
@OptIn(ExperimentalTestApi::class)
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilAustralianPatienceTableauExists(
    parentTT: String,
    card1: Card,
    card2: Card,
    card3: Card,
    card4: Card
) = waitUntilExactlyOneExists(
    hasTestTag(parentTT) and
            hasAnyChild(hasTestTag(card1.toString())) and
            hasAnyChild(hasTestTag(card2.toString())) and
            hasAnyChild(hasTestTag(card3.toString())) and
            hasAnyChild(hasTestTag(card4.toString()))
)

/**
 *  Waits until given [Card]s disappear under pile with given [parentTT] test tag. Uses default
 *  timeout of 1 second before failing. Used for testing Australian Patience
 */
@OptIn(ExperimentalTestApi::class)
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitUntilAustralianPatienceTableauDoesNotExist(
    parentTT: String,
    card1: Card,
    card2: Card,
    card3: Card,
    card4: Card
) = waitUntilDoesNotExist(
    hasTestTag(parentTT) and
            hasAnyChild(hasTestTag(card1.toString())) and
            hasAnyChild(hasTestTag(card2.toString())) and
            hasAnyChild(hasTestTag(card3.toString())) and
            hasAnyChild(hasTestTag(card4.toString()))
)

/**
 *  Performs click action at given [position].
 */
fun SemanticsNodeInteraction.performClickAt(position: Offset): SemanticsNodeInteraction {
    return performTouchInput {
        click(position)
    }
}