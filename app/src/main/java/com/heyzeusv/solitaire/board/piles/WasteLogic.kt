package com.heyzeusv.solitaire.board.piles

import com.heyzeusv.solitaire.util.Suits

/**
 *  In Solitaire, the Waste refers to the face up pile where cards drawn from [Stock] are placed.
 *  Only the top [Card] is playable. Not all games use a [Waste] pile.
 *
 *  @param initialPile Cards that [Pile] is initialized with.
 */
class Waste(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds a [Card], face up, to the end [truePile].
     *
     *  @param card The [Card] to be added.
     */
    override fun add(card: Card) {
        _truePile.add(card.copy(faceUp = true))
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Adds multiple [Cards][Card], all face up, to the end [truePile].
     *
     *  @param cards The [Cards][Card] to be added.
     */
    override fun addAll(cards: List<Card>) {
        _truePile.addAll(cards.map { it.copy(faceUp = true) })
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }


    /**
     *  Removes the last [Card] in [truePile], which would refer to the top showing [Card].
     *
     *  @param tappedIndex Not used in this version.
     *  @return The last [Card] of [truePile] or the Ace of Diamonds if empty. (Not Used)
     */
    override fun remove(tappedIndex: Int): Card {
        return if (_truePile.isEmpty()) {
            Card(0, Suits.DIAMONDS)
        } else {
            val removedCard = _truePile.removeLast()
            animatedPiles.add(_truePile.toList())
            appendHistory()
            return removedCard
        }
    }

    /**
     *  Resets [animatedPiles], [historyList], [truePile], and [displayPile].
     *
     *  @param cards Not used in this version.
     */
    override fun reset(cards: List<Card>) {
        resetLists()
        currentStep = emptyList()
    }

    /**
     *  Returns [truePile] to its previous state by accessing [historyList].
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    /**
     *  Used when [Stock] is pressed when it is empty, which causes all [Waste] [Cards][Card] to be
     *  removed.
     */
    fun removeAll() {
        _truePile.clear()
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }
}