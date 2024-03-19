package com.heyzeusv.solitaire.data.pile

import com.heyzeusv.solitaire.data.Card

/**
 *  In Solitaire, Stock refers to the face down pile where players draw from and place the drawn
 *  card on the [Waste] pile.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    fun getCards(amount: Int): List<Card> {
        val list = mutableListOf<Card>()
        for (i in 1..amount) {
            try {
                list.add(_truePile[i - 1])
            } catch (e: IndexOutOfBoundsException) {
                return list
            }
        }
        return list
    }

    /**
     *  Removes [amount] given and returns them as a list.
     */
    fun removeMany(amount: Int): List<Card> {
        val list = mutableListOf<Card>()
        for (i in 1..amount) {
            try {
                list.add(remove())
            } catch (e: NoSuchElementException) {
                return list
            }
        }
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return list
    }

    /**
     *  Add given [cards] to [_truePile].
     */
    override fun add(cards: List<Card>): Boolean {
        val success = _truePile.addAll(cards.map { it.copy(faceUp = false) })
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return success
    }

    /**
     *  Remove the first [Card] in [_truePile] and return it.
     */
    override fun remove(tappedIndex: Int): Card = _truePile.removeFirst()

    /**
     *  Reset [_truePile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.clear()
        add(cards)
        _displayPile.clear()
        _displayPile.addAll(_truePile.toList())
    }

    /**
     *  Used to return [_truePile] to a previous state.
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        // only last card is shown to user, this makes sure it is not visible
        if (history.isNotEmpty()) history[history.size - 1] = history.last().copy(faceUp = false)
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }

    fun recordHistory() { currentStep = _truePile.toList() }
}