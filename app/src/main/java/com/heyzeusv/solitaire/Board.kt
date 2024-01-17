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

    // runs when user taps on waste
    fun onWasteTap() {
        if (waste.isNotEmpty()) {
            // if any move is possible then remove card from waste
            if (legalMove(listOf(waste.last()))) waste.removeLast()
        }
    }

    // runs when user taps on foundation
    fun onFoundationTap(fIndex: Int) {
        val foundation = foundations[fIndex]
        if (foundation.pile.isNotEmpty()) {
            // if any move is possible then remove card from foundation
            if (legalMove(listOf(foundation.pile.last()))) foundation.removeCard()
        }
    }

    // runs when user taps on tableau
    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = tableau[tableauIndex]
        val tPile = tableauPile.pile
        if (tPile.isNotEmpty()) {
            if (legalMove(tPile.subList(cardIndex, tPile.size))) tableauPile.removeCards(cardIndex)
        }
    }

    /**
     *  Checks if move is possible by attempting to add [cards] to piles. Returns true if added.
     */
    private fun legalMove(cards: List<Card>): Boolean {
        if (cards.size == 1) {
            foundations.forEach {
                if (it.addCard(cards.first())) return true
            }
        }
        tableau.forEach {
            if (it.addCards(cards)) return true
        }
        return false
    }
}