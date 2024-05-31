package com.heyzeusv.solitaire.board.piles

/**
 *  In Solitaire, Stock refers to the face down pile where players draw from and place the drawn
 *  card on the [Waste] pile.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Returns a list of first [amount] of cards in [truePile]. Returns smaller list if [amount]
     *  is greater than [truePile] size.
     */
    fun getCards(amount: Int): List<Card> {
        val list = mutableListOf<Card>()
        for (i in 0 until amount) {
            try {
                list.add(_truePile[i])
            } catch (e: IndexOutOfBoundsException) {
                return list
            }
        }
        return list
    }

    /**
     *  Removes and returns a list of first [amount] of cards in [truePile]. Returns smaller list
     *  if [amount] is greater than [truePile] size.
     */
    fun removeMany(amount: Int): List<Card> {
        val list = mutableListOf<Card>()
        for (i in 1..amount) {
            try {
                list.add(remove())
            } catch (e: NoSuchElementException) {
                break
            }
        }
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
        return list
    }

    /**
     *  Add given [cards] to [truePile].
     */
    override fun addAll(cards: List<Card>) {
        _truePile.addAll(cards.map { it.copy(faceUp = false) })
        animatedPiles.add(_truePile.toList())
        appendHistory(_truePile.toList())
    }

    /**
     *  Remove the first [Card] in [truePile] and return it.
     */
    override fun remove(tappedIndex: Int): Card {
        val removedCard = _truePile.removeFirst()
        _displayPile.clear()
        _displayPile.addAll(_truePile.toList())
        return removedCard
    }

    /**
     *  Reset [truePile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        animatedPiles.clear()
        resetHistory()
        _truePile.clear()
        _truePile.addAll(cards)
        _displayPile.clear()
        _displayPile.addAll(_truePile.toList())
    }

    /**
     *  Used to return [truePile] to a previous state.
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

    /**
     *  Used to ensure currentStep is correctly updated after game has been fully reset.
     */
    fun recordHistory() { currentStep = _truePile.toList() }
}