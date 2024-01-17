package com.heyzeusv.solitaire

/**
 *  Game board information.
 */
class Board {

    val deck = Deck()
    // creates Foundation for each Suit
    val foundations = Suits.entries.map { Foundation(it) }
    val tableau = MutableList(7) { Tableau() }
    val waste = mutableListOf<Card>()

    // goes through all card piles in the game and resets them for a new game
    fun reset() {
        // shuffled 52 card deck
        deck.reset()
        // empty foundations
        foundations.forEach { it.resetCards() }
        // each pile in the tableau has 1 more card than the previous
        tableau.forEachIndexed { i, _ ->
            val cards = MutableList(i + 1) { deck.drawCard() }
            tableau[i] = Tableau(cards)
        }
        // clear the waste pile
        waste.clear()
    }

    // runs when user taps on deck
    fun onDeckTap() {
        // add card to waste if deck is not empty and flip it face up
        if (deck.gameDeck.isNotEmpty()) {
            waste.add(deck.drawCard().apply { faceUp = true })
        } else {
            // add back all cards from waste to deck
            deck.replace(waste)
            waste.clear()
        }
    }
}