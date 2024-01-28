package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DeckTest {

    private val deck = Deck()
    private val tc = TestCards

    @Test
    fun deckCreation() {
        val expectedDeck = tc.deck
        // call to reset is required in order to fill gameDeck
        deck.reset()

        // sort actual by first suit then value
        val actualDeck = deck.gameDeck.toList()
            .sortedWith(compareBy({ it.suit }, { it.value }))

        assertEquals(expectedDeck, actualDeck)
    }

    @Test
    fun deckDrawCard() {
        // deck is filled when reset is called
        deck.reset()
        val expectedCard = deck.gameDeck[0]

        val actualCard = deck.drawCard()

        assertEquals(expectedCard, actualCard)
    }

    @Test
    fun deckReplace() {
        val expectedDeck = mutableListOf(tc.card0C, tc.card11C, tc.card4S)

        deck.replace(expectedDeck)

        assertEquals(expectedDeck, deck.gameDeck.toList())
    }

    @Test
    fun deckReset() {
        deck.reset()
        val firstReset = deck.gameDeck.toList()

        deck.reset()
        val secondReset = deck.gameDeck.toList()

        // flimsy test since the chance of both resets being the same is not entirely 0%...
        assertNotEquals(firstReset, secondReset)
    }

    @Test
    fun deckUndo() {
        deck.reset()
        val expectedDeck = mutableListOf(tc.card0C, tc.card11C, tc.card4S)

        deck.undo(expectedDeck)

        assertEquals(expectedDeck, deck.gameDeck.toList())
    }

    @Test
    fun deckUndoNoCards() {
        deck.reset()

        deck.undo(emptyList())

        assert(deck.gameDeck.isEmpty())
    }

    @Test
    fun deckRestart() {
        deck.reset()
        val initialDeck = deck.gameDeck.toList()

        deck.restart()

        val restartDeck = deck.gameDeck.toList()

        assertEquals(initialDeck, restartDeck)
    }
}