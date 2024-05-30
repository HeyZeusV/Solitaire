package com.heyzeusv.solitaire.board.piles

/**
 *  In Solitaire, Waste refers to the face up pile where cards drawn from [Stock] are placed. Only
 *  the top [CardLogic] is playable.
 */
class Waste(initialPile: List<CardLogic> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds given [cards] to [truePile].
     */
    override fun add(cards: List<CardLogic>) {
        _truePile.addAll(cards.map { it.copy(faceUp = true) })
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Removes the last [CardLogic] in [truePile] which would refer to the top showing card and return it.
     */
    override fun remove(tappedIndex: Int): CardLogic {
        val removedCard = _truePile.removeLast()
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return removedCard
    }

    /**
     *  Used when [Stock] is pressed when it is empty, which causes all [Waste] [CardLogic]s to be
     *  removed.
     */
    fun removeAll() {
        _truePile.clear()
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Reset [truePile] using given [cards].
     */
    override fun reset(cards: List<CardLogic>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.clear()
        _displayPile.clear()
        currentStep = emptyList()
    }

    /**
     *  Used to return [truePile] to a previous state.
     */
    override fun undo() {
        _truePile.clear()
        val history = retrieveHistory()
        // makes sure all cards are face up
        history.map { it.copy(faceUp = true) }
        _truePile.addAll(history)
        animatedPiles.add(_truePile.toList())
        currentStep = _truePile.toList()
    }
}