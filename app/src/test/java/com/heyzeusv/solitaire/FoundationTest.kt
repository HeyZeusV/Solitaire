package com.heyzeusv.solitaire

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.util.Suits
import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationTest {

    private val foundation = Foundation(Suits.DIAMONDS)
    private val tc = TestCards

    @Test
    fun foundationAdd() {
        val expectedFoundation = listOf(tc.card0D, tc.card1D, tc.card2D, tc.card3D)

        foundation.add(listOf(tc.card0D))
        // card of wrong suit
        foundation.add(listOf(tc.card1H))
        foundation.add(listOf(tc.card1D))
        // card of wrong value
        foundation.add(listOf(tc.card3D))
        foundation.add(listOf(tc.card2D))
        foundation.add(listOf(tc.card3D))
        // card of wrong suit
        foundation.add(listOf(tc.card4S))
        // card of wrong suit and value
        foundation.add(listOf(tc.card1H))

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationRemove() {
        val expectedFoundation = listOf(tc.card0D, tc.card1D, tc.card2D)

        foundation.add(listOf(tc.card0D))
        foundation.add(listOf(tc.card1D))
        foundation.add(listOf(tc.card2D))
        foundation.add(listOf(tc.card3D))

        foundation.remove()

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationReset() {
        val expectedFoundation = emptyList<Card>()

        foundation.add(listOf(tc.card0D))
        foundation.add(listOf(tc.card1D))
        foundation.add(listOf(tc.card2D))
        foundation.add(listOf(tc.card3D))

        foundation.reset()

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationUndo() {
        val expectedFoundation = listOf(tc.card0D, tc.card1D, tc.card2D)

        foundation.undo(listOf(tc.card0D, tc.card1D, tc.card2D))

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationUndoEmptyCards() {
        val expectedFoundation = emptyList<Card>()

        foundation.add(listOf(tc.card0D))

        foundation.undo(emptyList())

        assertEquals(expectedFoundation, foundation.pile)
    }
}