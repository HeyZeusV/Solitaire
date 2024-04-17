package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationTest {

    private val foundation = Foundation(Suits.DIAMONDS)
    private val tc = TestCards

    @Test
    fun foundationAdd() {
        val expectedFoundation = listOf(tc.card1D, tc.card2D, tc.card3D, tc.card4D)

        foundation.add(listOf(tc.card1D))
        // card of wrong suit
        foundation.add(listOf(tc.card1H))
        foundation.add(listOf(tc.card2D))
        // card of wrong value
        foundation.add(listOf(tc.card4D))
        foundation.add(listOf(tc.card3D))
        foundation.add(listOf(tc.card4D))
        // card of wrong suit
        foundation.add(listOf(tc.card5S))
        // card of wrong suit and value
        foundation.add(listOf(tc.card2H))

        assertEquals(expectedFoundation, foundation.truePile)
    }

    @Test
    fun foundationRemove() {
        val expectedFoundation = listOf(tc.card1D, tc.card2D, tc.card3D)

        foundation.add(listOf(tc.card1D))
        foundation.add(listOf(tc.card2D))
        foundation.add(listOf(tc.card3D))
        foundation.add(listOf(tc.card4D))

        foundation.remove()

        assertEquals(expectedFoundation, foundation.truePile)
    }

    @Test
    fun foundationReset() {
        val expectedFoundation = emptyList<Card>()

        foundation.add(listOf(tc.card1D))
        foundation.add(listOf(tc.card2D))
        foundation.add(listOf(tc.card3D))
        foundation.add(listOf(tc.card4D))

        foundation.reset()

        assertEquals(expectedFoundation, foundation.truePile)
    }

    @Test
    fun foundationUndo() {
        val expectedFoundation = listOf(tc.card1D, tc.card2D, tc.card3D)

        foundation.undo(listOf(tc.card1D, tc.card2D, tc.card3D))

        assertEquals(expectedFoundation, foundation.truePile)
    }

    @Test
    fun foundationUndoEmptyCards() {
        val expectedFoundation = emptyList<Card>()

        foundation.add(listOf(tc.card1D))

        foundation.undo(emptyList())

        assertEquals(expectedFoundation, foundation.truePile)
    }
}