package com.heyzeusv.solitaire

import androidx.compose.runtime.mutableStateListOf

class Waste {

    private val _pile = mutableStateListOf<Card>()
    val pile: List<Card> get() = _pile

    /**
     *  Adds given [card] to [Waste] pile.
     */
    fun addCard(card: Card) = _pile.add(card)

    /**
     *  Removes the last card of the pile which would refer to the top showing card.
     */
    fun removeCard() = _pile.removeLast()

    /**
     *  Removes all card from the pile.
     */
    fun resetCards() = _pile.clear()
}