package com.heyzeusv.solitaire.util

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 *  Checks that node has text with given String resource [id] with [args] (if any).
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTextId(
    @StringRes id: Int,
    vararg args: Any?
): SemanticsNodeInteraction = onNodeWithText((activity.getString(id, *args)))

/**
 *  Checks that node has content description with given String resource [id] with [args] (if any).
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithConDescId(
    @StringRes id: Int,
    vararg args: Any?
): SemanticsNodeInteraction = onNodeWithContentDescription((activity.getString(id, *args)))

/**
 *  Performs click action at given [position].
 */
fun SemanticsNodeInteraction.performClickAt(position: Offset): SemanticsNodeInteraction {
    return performTouchInput {
        click(position)
    }
}