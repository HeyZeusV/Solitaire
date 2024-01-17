package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class DeckTest {

    private val deck = Deck()
    private val tc = TestCards

    // Checking if Deck initializes with 52 Cards with correct values and suits
    @Test
    fun deckCreation() {
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

    @Test
    fun deckDrawCard() {
        val expectedCard = deck.gameDeck[0]

        val actualCard = deck.drawCard()

        assertEquals(expectedCard, actualCard)
    }

    @Test
    fun deckReplace() {
        val expectedDeck = mutableListOf(tc.card0C, tc.card11C, tc.card4S)

        deck.replace(expectedDeck)

        assertEquals(expectedDeck, deck.gameDeck)
    }

    @Test
    fun deckReset() {
        val expectedDeck = tc.deck

        deck.reset()

        assert(expectedDeck == tc.deck)
    }
}