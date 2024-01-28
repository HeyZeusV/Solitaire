package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationTest {

    private val foundation = Foundation(Suits.DIAMONDS)
    private val tc = TestCards

    @Test
    fun foundationAddCard() {
        val expectedFoundation = listOf(tc.card0D, tc.card1D, tc.card2D, tc.card3D)

        foundation.addCard(tc.card0D)
        // card of wrong suit
        foundation.addCard(tc.card1H)
        foundation.addCard(tc.card1D)
        // card of wrong value
        foundation.addCard(tc.card3D)
        foundation.addCard(tc.card2D)
        foundation.addCard(tc.card3D)
        // card of wrong suit
        foundation.addCard(tc.card4S)
        // card of wrong suit and value
        foundation.addCard(tc.card1H)

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationRemoveCard() {
        val expectedFoundation = listOf(tc.card0D, tc.card1D, tc.card2D)

        foundation.addCard(tc.card0D)
        foundation.addCard(tc.card1D)
        foundation.addCard(tc.card2D)
        foundation.addCard(tc.card3D)

        foundation.removeCard()

        assertEquals(expectedFoundation, foundation.pile)
    }

    @Test
    fun foundationResetCards() {
        val expectedFoundation = emptyList<Card>()

        foundation.addCard(tc.card0D)
        foundation.addCard(tc.card1D)
        foundation.addCard(tc.card2D)
        foundation.addCard(tc.card3D)

        foundation.resetCards()

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

        foundation.addCard(tc.card0D)

        foundation.undo(emptyList())

        assertEquals(expectedFoundation, foundation.pile)
    }
}