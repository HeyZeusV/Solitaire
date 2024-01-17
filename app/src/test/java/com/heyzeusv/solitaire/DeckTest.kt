package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class DeckTest {

    private val deck = Deck()

    // Checking if Deck initializes with 52 Cards with correct values and suits
    @Test
    fun initialDeckCreation() {
        val expectedDeck = mutableListOf<Card>()
        // manually create 13 Cards of each suit and adds it to expected deck
        Suits.entries.forEach {
            for (i in 0 until 13) {
                val card = Card(i, it)
                expectedDeck.add(card)
            }
        }

        assertEquals(expectedDeck, deck.gameDeck)
    }
}