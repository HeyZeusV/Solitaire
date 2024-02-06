package com.heyzeusv.solitaire

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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
 *  Checks for all nodes that have content description with given String resource [id] with [args]
 *  (if any).
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onAllNodesWithConDescId(
    @StringRes id: Int,
    vararg args: Any?
): SemanticsNodeInteractionCollection = onAllNodes(hasContentDescription(activity.getString(id, *args)))