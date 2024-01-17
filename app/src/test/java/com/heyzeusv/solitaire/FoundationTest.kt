package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationTest {

    private val foundation = Foundation(Suits.DIAMONDS)

    // checking that cards are correctly added based on suit and value
    @Test
    fun foundationAddCards() {
        val card0D = Card(0, Suits.DIAMONDS)
        val card1D = Card(1, Suits.DIAMONDS)
        val card2D = Card(2, Suits.DIAMONDS)
        val card3D = Card(3, Suits.DIAMONDS)
        val card1H = Card(1, Suits.HEARTS)
        val card4S = Card(4, Suits.SPADES)

        val expectedFoundation = listOf(card0D, card1D, card2D, card3D)

        foundation.addCard(card0D)
        // card of wrong suit
        foundation.addCard(card1H)
        foundation.addCard(card1D)
        // card of wrong value
        foundation.addCard(card3D)
        foundation.addCard(card2D)
        foundation.addCard(card3D)
        // card of wrong suit
        foundation.addCard(card4S)
        // card of wrong suit and value
        foundation.addCard(card1H)

        assertEquals(expectedFoundation, foundation.pile)

    }
}