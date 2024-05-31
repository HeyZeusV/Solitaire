package com.heyzeusv.solitaire.board.piles

/**
 *  In Solitaire, the Stock refers to the pile where users draw more [Cards][Card] from.
 *
 *  @param initialPile Cards that [Pile] is initialized with.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Adds a [Card], face down, to the end [truePile].
     *
     *  @param card The [Card] to be added.
     */
    override fun add(card: Card) {
        _truePile.add(card.copy(faceUp = false))
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Adds multiple [Cards][Card], all face down, to the end [truePile].
     *
     *  @param cards The [Cards][Card] to be added.
     */
    override fun addAll(cards: List<Card>) {
        _truePile.addAll(cards.map { it.copy(faceUp = false) })
        animatedPiles.add(_truePile.toList())
        appendHistory()
    }

    /**
     *  Removes the first [Card] in [truePile], which would refer to the top showing [Card].
     *
     *  @param tappedIndex Not used in this version.
     *  @return The first [Card] of [truePile] or the Ace of Diamonds if empty. (Not Used)
     */
    override fun remove(tappedIndex: Int): Card {
        val removedCard = _truePile.removeFirst()
        _displayPile.clear()
        _displayPile.addAll(_truePile.toList())
        return removedCard
    }

    /**
     *  Resets [animatedPiles] and [historyList]. Replaces [truePile] and [displayPile] with a new
     *  list of face up [Cards][Card].
     *
     *  @param cards The new list of [Cards][Card].
     */
    override fun reset(cards: List<Card>) {
        resetLists()
        if (cards.isNotEmpty()) {
            val flippedCards = cards.map { it.copy(faceUp = false) }
            _truePile.addAll(flippedCards)
            _displayPile.addAll(flippedCards)
        }
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
     *  Retrieves an amount of [Cards][Card] from the beginning of [truePile] and removes them.
     *
     *  @param amount The amount to attempt to return. Returns less if [truePile] size is less than.
     *  @return The amount of [Cards][Card] available from the beginning of [truePile].
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
        appendHistory()
        return list
    }

    /**
     *  Used to ensure currentStep is correctly updated after game has been fully reset.
     */
    fun recordHistory() { currentStep = _truePile.toList() }
}