package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationTest {

    private val foundation = Foundation(Suits.DIAMONDS)
    private val tc = TestCards

    // checking that cards are correctly added based on suit and value
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
}