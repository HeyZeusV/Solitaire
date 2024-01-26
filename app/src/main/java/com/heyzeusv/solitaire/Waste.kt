package com.heyzeusv.solitaire

import androidx.compose.runtime.mutableStateListOf

class Waste {

    private val _pile: MutableList<Card> = mutableStateListOf()
    val pile: List<Card> get() = _pile

    /**
     *  Adds given [card] to [Waste] pile.
     */
    fun addCard(card: Card) { _pile.add(card) }

    /**
     *  Removes the last card of the pile which would refer to the top showing card.
     */
    fun removeCard(): Card = _pile.removeLast()

    /**
     *  Removes all card from the pile.
     */
    fun resetCards() = _pile.clear()

    /**
     *  Used to return [_pile] to a previous state of given [cards].
     */
    fun undo(cards: List<Card>) {
        _pile.clear()
        if (cards.isEmpty()) return
        // makes sure all cards are face up
        _pile.addAll(cards.onEach { it.faceUp = true })
    }

    override fun toString(): String = pile.toList().toString()
}